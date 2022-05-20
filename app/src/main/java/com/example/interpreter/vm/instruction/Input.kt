package com.example.interpreter.vm.instruction

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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
import kotlin.coroutines.suspendCoroutine


@Serializable
class Input : Instruction {
    @Transient
    var context: AppCompatActivity? = null
    
    override fun exec(env: Env) = sequence<Instruction> {
        yield(this@Input)
        
        //todo: Deferred
        
        val callable = Callable<kotlin.String> {
/*
            Thread.sleep(10000)
*/
            
            runBlocking {
                suspendCoroutine<kotlin.String> {
                    launch {
                        delay(10000)
                    }.start()
                    
                    it.resumeWith(Result.success<kotlin.String>("dsf"))
                }
            }
            
            return@Callable "df"
        }
        
        val task = FutureTask(callable)
        context!!.runOnUiThread(task)
        
        Log.i(TAG, "отлагало ${task.get()}")
        
    }.iterator()
    
    constructor(compiler: Compiler, context: AppCompatActivity? = null) : super(compiler) {
        this.context = context
    }
}