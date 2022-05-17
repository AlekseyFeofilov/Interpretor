package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.Env
import kotlinx.serialization.Serializable

@Serializable
open class Register : Instruction {
    private val value: Instruction
    private val objItem: kotlin.String
    private val env: Env?
    
    //todo: recursive unregistering, return any, but not register
    override fun exec(env: Env) = sequence<Instruction> {
        val reg = env.reg(value) ?: throw Error("Execution failed, register is null")
        
        if(reg !is Object){
//            throw Error("Return is not object")
            yield(reg)
            return@sequence
        }
        
        yield(reg[objItem] ?: throw Error("Object define is null"))
    }.iterator()
    
    fun exec(): Iterator<Instruction> { return exec(env ?: throw Error("Env is null")) }
    
    constructor(compiler: Compiler, value: Instruction, objItem: kotlin.String = "out", env: Env? = null) : super(compiler) {
        this.value = value
        this.objItem = objItem
        this.env = env
    }
}