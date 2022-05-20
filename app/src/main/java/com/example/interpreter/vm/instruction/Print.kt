package com.example.interpreter.vm.instruction

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.interpreter.WorkspaceFragment
import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.Env
import com.example.interpreter.vm.awaitLR
import com.example.interpreter.vm.yieldAllLR
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Suppress("RemoveRedundantQualifierName", "MemberVisibilityCanBePrivate")
@Serializable
open class Print : Instruction {
    val value: Instruction
    val color: kotlin.String
    val ln: kotlin.Boolean
    
    @Transient
    var context: WorkspaceFragment? = null
    
    override fun exec(env: Env) = sequence<Instruction> {
        val str = yieldAllLR(_unRegister(value, env).exec(env)).toString()
        
        Log.i(TAG, str)
        
        context?.activity?.runOnUiThread {
            if(context != null) {
                when (ln) {
                    //true -> context?.printToConsole(str, color)
                    false -> context?.printlnToConsole(str, color)
                }
            }
        }
        
        yield(this@Print)
    }.iterator()
    
    private fun _unRegister(value: Instruction, env: Env): Instruction{
        if(value is Register) return _unRegister(awaitLR(value.exec(env)), env)
        
        return value
    }
    
    constructor(compiler: Compiler, value: Instruction, ln: kotlin.Boolean = false, color: kotlin.String = "#ffffff") : super(compiler) {
        this.context = compiler.context
        this.color = color
        this.value = value
        this.ln = ln
    }
}