package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.Env
import com.example.interpreter.vm.awaitLR
import com.example.interpreter.vm.yieldAllLR
import kotlinx.serialization.Serializable

@Serializable
open class Register : Instruction {
    private val value: Instruction
    private val objItem: kotlin.String
    private val env: Env?
    private val _exec: kotlin.Boolean
    
    override fun exec(env: Env) = sequence<Instruction> {
        when(value){
            is Bool -> { yield(value); return@sequence }
            is Int -> { yield(value); return@sequence }
            is Number -> { yield(value); return@sequence }
            is String -> { yield(value); return@sequence }
        }
        
        if(_exec) env.reg(value, yieldAllLR(value.exec(env)))
        val reg = env.reg(value) ?:
            throw Error("Execution failed, register [${value.types}@${value.id}] is null")
        
        if(reg !is Object){
//            throw Error("Return is not object")
            yield(reg)
            return@sequence
        }
        
        yield(reg[objItem] ?: throw Error("Object define is null"))
    }.iterator()
    
    fun exec(): Iterator<Instruction> { return exec(env ?: throw Error("Env is null")) }
    
    constructor(compiler: Compiler, value: Instruction, objItem: kotlin.String = "out", env: Env? = null, exec: kotlin.Boolean = false) : super(compiler) {
        this.value = value
        this.objItem = objItem
        this.env = env
        this._exec = exec
    }
}