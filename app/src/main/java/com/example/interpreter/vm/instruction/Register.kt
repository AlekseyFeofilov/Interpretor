package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Env
import kotlinx.serialization.Serializable

@Serializable
open class Register(val value: Instruction, private val objItem: kotlin.String, val env: Env? = null) : Instruction() {
    override fun exec(env: Env) = sequence<Instruction> {
        val reg = env.reg(value) ?: throw Error("Execution failed, register is null")
        
        if(reg !is Object) throw Error("Return is not object")
        
        yield(reg[objItem] ?: throw Error("Object define is null"))
    }.iterator()
    
    fun exec(): Iterator<Instruction> { return exec(env ?: throw Error("Env is null")) }
}