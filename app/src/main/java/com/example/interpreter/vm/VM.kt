package com.example.interpreter.vm

import android.util.Log
import com.example.interpreter.vm.instruction.*
import com.example.interpreter.vm.instruction.Number
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import java.time.Duration
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.LinkedHashSet
import kotlin.concurrent.thread
import kotlin.reflect.jvm.jvmName

@Suppress("MemberVisibilityCanBePrivate", "RemoveExplicitTypeArguments",
    "RemoveEmptyPrimaryConstructor", "FunctionName"
)
class VM {
    lateinit var block: Executor
    
    constructor(instructions: List<Instruction>){
        this.block = Executor(Env(), instructions)
    }
    
    constructor(block: Executor){
        this.block = block
    }
    
    constructor(@Suppress("UNUSED_PARAMETER") Data: String){
        TODO("write JSON parser/stringifier for instruction")
        @Suppress("UNREACHABLE_CODE")
        this.block = Json.decodeFromString<Executor>(Data)
    }
    // todo: new instruction "input" input all types, multiple init
    constructor(){ // For tests
//        Log.i("VM", this.block.toString())
//        Log.i("VM", this.Tree[0].Type ?: "")
//        Log.i("VM", this.Tree[1].Type ?: "")
//        Log.i("VM", this.Tree[2].Type ?: "")
//        Log.i("VM", Json.encodeToString(this.Tree))
        
        val compiler = Compiler.FCompiler()
        
        val math = Math(compiler, "sin PI min cos PI")
        val math2 = Math(compiler, listOf(
            Math.TLBrk(),
            Math.TRegister(Register(compiler, math, "out")),
            Math.TPlus(),
            Math.TRegister(Register(compiler, math, "out")),
            Math.TRBrk(),
            Math.TMul(),
            Math.TNumber(Number(compiler, 2.0))
        ))
        
        val env = Env()
        
        this.block = Executor(env, listOf<Instruction>(
            SetVar(compiler, "two", Number(compiler, 6.0), true),
            math,
            math2,
            SetVar(compiler, "da", Register(compiler, math, "out"), true),
            SetVar(compiler, "da1", Register(compiler, math2, "out"), true),
            Print(compiler, GetVar(compiler, "two")),
            Print(compiler, GetVar(compiler, "da")),
            Print(compiler, GetVar(compiler, "da1")),
            If(compiler, listOf<Executor>(
                Executor(Env(env), listOf(Bool(compiler, Register(compiler, math, "out", env)))),
                Executor(Env(env), listOf(Print(compiler, String(compiler, "if true")))),
                Executor(Env(env), listOf(Print(compiler, String(compiler, "if false")))),
            )),
            Number(compiler),
            Nop(compiler)
        ))
        
//        Log.i("VM", Json.encodeToString(math))
        
        start()
    }
    
    fun start(debug: Boolean = false){
        _DEBUGGER(debug)
    }
    
    private data class Debug(
        val type: Type = Type.NONE
    ){
        enum class Type {
            NONE,
            START_DEBUG,
            STOP_DEBUG,
            START,
            STOP,
            NEXT,
            RESTART,
        }
    }
    
    private class WSConnects(){
        companion object {
            val wsConnects: MutableSet<Connection> = Collections.synchronizedSet<Connection?>(LinkedHashSet())
            
            suspend fun sendAll(str: String) = wsConnects.iterator().forEach { it.session.send(str) }
            fun add(value: Connection) = wsConnects.add(value)
            fun remove(value: Connection) = wsConnects.remove(value)
            fun any() = wsConnects.any()
        }
    }
    
    private class Connection(val session: DefaultWebSocketSession) {
        companion object {
            var lastId = AtomicInteger(0)
        }
        
        val name = "user-${lastId.getAndIncrement()}"
    }
    
    private fun _DEBUGGER(debug: Boolean) = thread(
        start = true,
        isDaemon = false,
        contextClassLoader = null,
        name = "VM"
    ) {
        runBlocking {
            val channelDebug = Channel<Debug>(10)
            var vm = _VM()
    
            embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
                install(WebSockets) {
                    pingPeriod = Duration.ofSeconds(15)
                    timeout = Duration.ofSeconds(15)
                    maxFrameSize = Long.MAX_VALUE
                    masking = false
                }
        
                routing {
                    route("/api") {
                        webSocket("/ws") {
                            val connection = Connection(this)
                    
                            if (!WSConnects.any()) channelDebug.send(Debug(Debug.Type.START_DEBUG))
                            WSConnects.add(connection)
    
                            Log.i("VM", "onConn: ${connection.name}")
                    
                            try {
                                for (frame in incoming) {
                                    val msg = (frame as Frame.Text).readText()
                            
                                    Log.i("VM", "onMsg[${connection.name}]: $msg")
                            
                                    channelDebug.send(
                                        when (msg) {
                                            "next" -> Debug(Debug.Type.NEXT)
                                            "stop" -> Debug(Debug.Type.STOP)
                                            "start" -> Debug(Debug.Type.START)
                                            "restart" -> Debug(Debug.Type.RESTART)
                                            else -> Debug(Debug.Type.NONE)
                                        }
                                    )
                                }
                            } catch (e: ClosedReceiveChannelException) {
                                WSConnects.remove(connection)
                                if (!WSConnects.any()) channelDebug.send(Debug(Debug.Type.STOP_DEBUG))
                        
                                Log.i("VM", "onClose ${closeReason.await()}")
                            } catch (e: Throwable) {
                                WSConnects.remove(connection)
                                if (!WSConnects.any()) channelDebug.send(Debug(Debug.Type.STOP_DEBUG))
                        
                                Log.i("VM", "onError ${closeReason.await()}")
                                e.printStackTrace()
                            }
                        }
                    }
                    
                    resource("/", "web/index.html")
                    
                    static("/static") {
                        resources("web/static")
                    }
                }
            }.start()

//        var i = 0
            var pause: Boolean = debug
    
            while (true) {
                var boolNext = false
                val dToken = channelDebug.tryReceive().getOrNull()
        
                if (dToken != null) {
                    when (dToken.type) {
                        Debug.Type.STOP -> {
                            pause = true
                        }
                        Debug.Type.START -> {
                            pause = false
                        }
                        Debug.Type.NEXT -> {
                            boolNext = true
                        }
                        Debug.Type.RESTART -> {
                            vm = _VM()
                        }
                        else -> {}
                    }
                }
        
                if (vm.hasNext() && (!pause || boolNext)) {
                    val lastInst = vm.next()
                    delay(100)
                    
                    val jsonObj = Json.encodeToString(
                        buildJsonObject {
                            put("instruction", JsonPrimitive(lastInst::class.jvmName + "@" + lastInst.id.toString()))
                            put("env", Json.encodeToJsonElement(block.env))
                            if(lastInst is Print){
                                put("console", JsonPrimitive(awaitLR(lastInst.value.exec(block.env)).toString()))
                            }
                        }
                    )
                    
                    Log.i("VM", jsonObj)
                    
                    launch { WSConnects.sendAll(jsonObj) }.join()
                } else delay(50)
                
//            Log.i("VM", "debugCaller")
//            Thread.sleep(50)

//            if(i++ >= 10)
//                exitProcess(0)
            }
        }
    }
    
    private fun _VM() = sequence {
        yieldAll(block.exec())
    }.iterator()
}

@Suppress("RemoveRedundantQualifierName")
suspend fun kotlin.sequences.SequenceScope<Instruction>.yieldAllLR(iterator: Iterator<Instruction>): Instruction {
    var last: Instruction? = null
    
    while (iterator.hasNext()){
        last = iterator.next()
        yield(last)
    }
    
    return last ?: Nop(Compiler.FCompiler())
}

fun awaitLR(iterator: Iterator<Instruction>): Instruction {
    var last: Instruction? = null
    
    while (iterator.hasNext()) last = iterator.next()
    
    return last ?: Nop(Compiler.FCompiler())
}