package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.Env
import kotlinx.serialization.Serializable

@Serializable
class Nop : Instruction {
    override fun exec(env: Env) = sequence<Instruction> { yield(this@Nop) }.iterator()
    
    constructor(compiler: Compiler) : super(compiler)
}