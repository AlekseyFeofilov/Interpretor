package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.Env
import com.example.interpreter.vm.yieldAllLR
import kotlinx.serialization.Serializable

@Serializable
class Time : Instruction  {
    override fun exec(env: Env) = sequence<Instruction> {
        yield(Object(Compiler.FCompiler(),
            "out" to Int(Compiler.FCompiler(), System.nanoTime().toInt())
        ))
    }.iterator()
    
    constructor(compiler: Compiler) : super(compiler) { }
}