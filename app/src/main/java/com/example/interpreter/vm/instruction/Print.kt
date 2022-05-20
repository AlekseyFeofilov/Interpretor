package com.example.interpreter.vm.instruction

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.interpreter.WorkspaceFragment
import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.Env
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
        val str = yieldAllLR(value.exec(env)).toString()
        
        Log.i(TAG, str)
        
        if(context != null) {
            when (ln) {
                true -> context?.printToConsole(str, color)
                false -> context?.printlnToConsole(str, color)
            }
        }
        
        yield(this@Print)
    }.iterator()
    
    constructor(compiler: Compiler, value: Instruction, ln: kotlin.Boolean = false, color: kotlin.String = "#ffffff") : super(compiler) {
        this.context = compiler.context
        this.color = color
        this.value = value
        this.ln = ln
    }
}