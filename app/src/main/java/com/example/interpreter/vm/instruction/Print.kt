package com.example.interpreter.vm.instruction

import android.util.Log
import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.Env
import com.example.interpreter.vm.yieldAllLR
import kotlinx.serialization.Serializable

@Serializable
open class Print : Instruction {
    val value: Instruction
    
    override fun exec(env: Env) = sequence<Instruction> {
        Log.i(TAG, yieldAllLR(value.exec(env)).toString())
        yield(this@Print)
    }.iterator()
    
    constructor(compiler: Compiler, value: Instruction) : super(compiler) { this.value = value }
}