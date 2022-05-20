package com.example.interpreter.vm.instruction

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.interpreter.WorkspaceFragment
import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.Env
import io.netty.util.concurrent.Promise
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.concurrent.Callable
import java.util.concurrent.FutureTask
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@Serializable
class Input : Instruction {
    @Transient
    var context:  WorkspaceFragment? = null
    
    override fun exec(env: Env) = sequence<Instruction> {
//        val callable = Callable<kotlin.String> {
//            val context = context!!
//
//            if(context.listOfReading.firstOrNull() != null){
//                return@Callable context.listOfReading.removeFirst()
//            }
//
//            do {
//                runBlocking {
//                    suspendCoroutine<kotlin.Int> {
//                        context.consoleEvent = it
//                    }
//                }
//            }while (context.listOfReading.firstOrNull() == null)
//
//            return@Callable context.listOfReading.removeFirst()
//        }
//
//        val task = FutureTask(callable)
        val str: kotlin.String
        
        runBlocking {
            str = suspendCoroutine<kotlin.String> {
                context!!.requireActivity().runOnUiThread{
                    if(context!!.listOfReading.firstOrNull() != null){
                        it.resume(context!!.listOfReading.removeFirst())
                        return@runOnUiThread
                    }
                    
                    context!!.consoleEvent = it
                }
            }
        }
        
//        context!!.requireActivity().runOnUiThread(task)
        
        yield(Object(Compiler.FCompiler(),
            "out" to String(Compiler.FCompiler(), str) //String(Compiler.FCompiler(), task.get())
        ))
    }.iterator()
    
    //todo: compiler, class var, name var
    
    constructor(compiler: Compiler) : super(compiler) {
        this.context = compiler.context
    }
}