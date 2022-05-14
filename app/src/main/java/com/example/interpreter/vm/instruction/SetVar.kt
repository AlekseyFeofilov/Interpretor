package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.Env
import com.example.interpreter.vm.yieldAllLR
import kotlinx.serialization.Serializable

@Serializable
class SetVar : Instruction  {
    private val name: kotlin.String
    private val instruction: Instruction
    private val define: Boolean
    
    override fun exec(env: Env) = sequence<Instruction> {
        if(define) {
            env.new(name, yieldAllLR(instruction.exec(env)))
        }else env[name] = yieldAllLR(instruction.exec(env))
        
        yield(this@SetVar)
    }.iterator()
    
    constructor(compiler: Compiler, name: kotlin.String, value: Instruction, define: Boolean = false) : super(compiler) {
        this.name = name
        this.instruction = value
        this.define = define
    }
}