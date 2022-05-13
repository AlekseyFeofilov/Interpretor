package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Env
import kotlinx.serialization.Serializable
// todo: register construct is env and not env, exec() and exec(env)
@Serializable
open class Register(val value: Instruction, val objItem: kotlin.String) : Instruction() {
    override fun exec(env: Env) = sequence<Instruction> {
        val reg = env.reg(value) ?: throw Error("Execution failed, register is null")
        
        if(reg !is Object) throw Error("Return is not object")
        
        yield(reg[objItem] ?: throw Error("Object define is null"))
    }.iterator()
    
}