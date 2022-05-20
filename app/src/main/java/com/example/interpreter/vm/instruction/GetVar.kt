package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.Env
import kotlinx.serialization.Serializable

@Serializable
class GetVar : Instruction {
    val name: kotlin.String
    
    override fun exec(env: Env) = sequence<Instruction> {
        yield(Object(Compiler.FCompiler(),
            "out" to env[name]
        ))
    }.iterator()
    
    constructor(compiler: Compiler, name: kotlin.String) : super(compiler) { this.name = name }
}