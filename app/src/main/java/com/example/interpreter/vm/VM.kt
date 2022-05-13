package com.example.interpreter.vm

import android.util.JsonToken
import android.util.Log
import com.example.interpreter.vm.instruction.*
import com.example.interpreter.vm.instruction.Number
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.util.Identity.encode
import io.ktor.websocket.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import kotlinx.serialization.json.Json.Default.encodeToJsonElement
import kotlinx.serialization.serializer
import org.json.JSONObject
import java.time.Duration
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.LinkedHashSet
import kotlin.concurrent.thread
import kotlin.reflect.jvm.jvmName
import kotlin.system.exitProcess

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
    
        val math = Math("two pow two / sqrt 4")
        val math2 = Math(listOf(
            Math.TLBrk(),
            Math.TRegister(Register(math, "out")),
            Math.TPlus(),
            Math.TRegister(Register(math, "out")),
            Math.TRBrk(),
            Math.TMul(),
            Math.TNumber(Number(2.0))
        ))
        
        val env = Env()
        
        this.block = Executor(env, listOf(
            SetVar("two", Number(6.0), true),
            math,
            math2,
            SetVar("da", Register(math, "out"), true),
            SetVar("da1", Register(math2, "out"), true),
            Print(GetVar("two")),
            Print(GetVar("da")),
            Print(GetVar("da1")),
            If(listOf<Executor>(
                Executor(Env(env), listOf(Bool(false))), // todo: test Register(env, math, "out")
                Executor(Env(env), listOf(Print(String("if true")))),
                Executor(Env(env), listOf(Print(String("if false")))),
            )),
            Number(),
            Nop()
        ))
        
//        Log.i("VM", Json.encodeToString(math))
        
        start(true)
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
                                put("console", JsonPrimitive(lastInst.value.exec(block.env).let { var last: Instruction = Nop(); while(it.hasNext()){ last = it.next() }; last; }.toString()))
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

suspend fun kotlin.sequences.SequenceScope<Instruction>.yieldAllLR(iterator: Iterator<Instruction>): Instruction {
    var last: Instruction = Nop()
    
    while (iterator.hasNext()){
        last = iterator.next()
        yield(last)
    }
    
    return last
}

fun awaitLR(iterator: Iterator<Instruction>): Instruction {
    var last: Instruction = Nop()
    
    while (iterator.hasNext()) last = iterator.next()
    
    return last
}