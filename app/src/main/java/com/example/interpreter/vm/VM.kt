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
import io.netty.resolver.DefaultHostsFileEntriesResolver
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import java.lang.Exception
import java.time.Duration
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.LinkedHashSet
import kotlin.concurrent.thread
import kotlin.reflect.jvm.jvmName
import kotlin.system.exitProcess

@Suppress("MemberVisibilityCanBePrivate", "RemoveExplicitTypeArguments",
    "RemoveEmptyPrimaryConstructor", "FunctionName"
)
class VM {
    lateinit var block: Executor
    
    constructor(instructions: MutableList<Instruction>){
        this.block = Executor(Env(), instructions)
    }
    
    constructor(block: Executor){
        this.block = block
    }
    
    // todo: new instruction "input" input all types, multiple init
    constructor(){
        val compiler = Compiler.FCompiler()
        
//        val math = Math(compiler, "sin PI min cos PI")
//        val math2 = Math(compiler, listOf(
//            Math.TLBrk(),
//            Math.TRegister(Register(compiler, math, "out")),
//            Math.TPlus(),
//            Math.TRegister(Register(compiler, math, "out")),
//            Math.TRBrk(),
//            Math.TMul(),
//            Math.TNumber(Number(compiler, 2.0))
//        ))
        
        val env = Env()
        
//        this.block = Executor(env, listOf<Instruction>(
//            SetVar(compiler, "two", Number(compiler, 6.0), true),
//            math,
//            math2,
//            SetVar(compiler, "da", Register(compiler, math, "out"), true),
//            SetVar(compiler, "da1", Register(compiler, math2, "out"), true),
//            Print(compiler, GetVar(compiler, "two")),
//            Print(compiler, GetVar(compiler, "da")),
//            Print(compiler, GetVar(compiler, "da1")),
//            If(compiler, listOf<Executor>(
//                Executor(Env(env), listOf(Bool(compiler, Register(compiler, math, "out", env)))),
//                Executor(Env(env), listOf(Print(compiler, String(compiler, "if true")))),
//                Executor(Env(env), listOf(Print(compiler, String(compiler, "if false")))),
//            )),
//            Number(compiler),
//            Nop(compiler)
//        ))
        /*
            function fitness(n){
                let a = 0, b = 1;
                
                for(let i = 0; i < n; i++)
                    [a, b] = [b, a + b];
                
                return a;
            }
         */
        
//        if(true){
//            val time1 = System.nanoTime()
//            for(ff in 0 until 10000) {
//                val n = 20
//                var a = 0
//                var b = 1
//
//
//                for (i in 0 until n) {
//                    val c = a
//                    a = b
//                    b += c
//                }
//            }
//            val time2 = System.nanoTime()
//
////            Log.i("VM", a.toString())
//            Log.i("VM", ((time2 - time1) / 1_000_000_000).toString())
//        }
//
//        return ;
    
        val timeEnv1 = Env(env)
        val timeEnv2 = Env(env)
        val timeIf = Math(compiler, "for < 10000")
        val timeAdd = Math(compiler, "for + 1")
        
        val forIf = Math(compiler, "i < n")
        val forEnv = Env(timeEnv2)
        val forBody = Env(timeEnv2)
        val mathB = Math(compiler, "c + b")
        val mathI = Math(compiler, "i + 1")
        val time1 = Time(compiler)
        val time2 = Time(compiler)
        
        this.block = Executor(env, listOf<Instruction>(
            time1,
            SetVar(compiler, "for", Number(compiler), true),
            
            SetVar(compiler, "n", Int(compiler, 20), true),
    
            SetVar(compiler, "a", Int(compiler, 0), true),
            SetVar(compiler, "b", Int(compiler, 1), true),
            SetVar(compiler, "c", Int(compiler, 0), true),
            SetVar(compiler, "i", Int(compiler, 0), true),
            
            While(compiler, listOf<Executor>(
                Executor(timeEnv1, listOf<Instruction>(timeIf, Bool(compiler, Register(compiler, timeIf, env = timeEnv1))), false), // is true localEnv clear error for RT
                Executor(timeEnv2, listOf<Instruction>(
                    While(compiler, listOf<Executor>(
                        Executor(forEnv, listOf<Instruction>(forIf, Bool(compiler, Register(compiler, forIf, env = forEnv))), false),
                        Executor(forBody, listOf<Instruction>(
                            SetVar(compiler, "c", GetVar(compiler, "a")),
                            SetVar(compiler, "a", GetVar(compiler, "b")),
                            
                            mathB,
                            SetVar(compiler, "b", Register(compiler, mathB)),
            
                            mathI,
                            SetVar(compiler, "i", Register(compiler, mathI)),
                        ), true),
                    )),
    
                    timeAdd,
                    SetVar(compiler, "for", Register(compiler, timeAdd)),
                ), true),
            )),
            
            time2,
            Print(compiler, GetVar(compiler, "a")),
            Print(compiler, Math(compiler, listOf<Math.Token>(
                Math.TLBrk(),
                Math.TRegister(Register(compiler, time2)),
                Math.TMinus(),
                Math.TRegister(Register(compiler, time1)),
                Math.TRBrk(),
                Math.TDiv(),
                Math.TNumber(Number(Compiler.FCompiler(), 1_000_000_000.0))
            )))
        ))
        
        Log.i("VM", Json{ prettyPrint = true; serializersModule = Math.module }.encodeToString(this.block))
        
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
            
            if(debug) {
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
                }.start() //todo: server for debug, double start crash app, port already in use
            }

//        var i = 0
            var pause: Boolean = debug
            try {
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
            
                        if (debug) {
                            val jsonObj = Json.encodeToString(
                                buildJsonObject {
                                    put(
                                        "instruction",
                                        JsonPrimitive(lastInst::class.jvmName + "@" + lastInst.id.toString())
                                    )
                                    put("env", Json.encodeToJsonElement(block.env))
                                    if (lastInst is Print) {
                                        put(
                                            "console",
                                            JsonPrimitive(awaitLR(lastInst.value.exec(block.env)).toString())
                                        )
                                    }
                                }
                            )
                
                            Log.i("VM", jsonObj)
                
                            launch { WSConnects.sendAll(jsonObj) }.join()
                        }
                    } else delay(50)

//            Log.i("VM", "debugCaller")
//            Thread.sleep(50)

//            if(i++ >= 10)
//                exitProcess(0)
                }
            }catch(e: Error) {
                e.printStackTrace()
            }catch(e: Exception) {
                e.printStackTrace()
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