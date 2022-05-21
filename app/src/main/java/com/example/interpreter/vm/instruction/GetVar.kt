package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.Env
import kotlinx.serialization.Serializable

@Serializable
class GetVar : Instruction {
    val name: kotlin.String
    
    override fun exec(env: Env) = sequence<Instruction> {
        val obj = env[name]
        if(obj is Object) obj.is_obj = false
        
        yield(obj)
    }.iterator()
    
    constructor(compiler: Compiler, name: kotlin.String) : super(compiler) { this.name = name }
}