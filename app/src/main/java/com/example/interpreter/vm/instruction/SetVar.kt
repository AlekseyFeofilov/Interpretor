package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.Env
import com.example.interpreter.vm.awaitLR
import com.example.interpreter.vm.yieldAllLR
import kotlinx.serialization.Serializable

@Serializable
class SetVar : Instruction  {
    private val name: kotlin.String
    private val instruction: Instruction
    private val define: Boolean
    
    override fun exec(env: Env) = sequence<Instruction> {
        if(define) {
            env.new(name, yieldAllLR(_unRegister(instruction, env).exec(env)))
        }else env[name] = yieldAllLR(_unRegister(instruction, env).exec(env))
        
        yield(this@SetVar)
    }.iterator()
    
    private fun _unRegister(value: Instruction, env: Env): Instruction{
        if(value is Register) return _unRegister(awaitLR(value.exec(env)), env)
        
        return value
    }
    
    constructor(compiler: Compiler, name: kotlin.String, value: Instruction, define: Boolean = false) : super(compiler) {
        this.name = name
        this.instruction = value
        this.define = define
    }
}