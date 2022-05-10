package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Env
import com.example.interpreter.vm.yieldAllLR
import kotlinx.serialization.Serializable

@Serializable
class SetVar(val name: kotlin.String, val instruction: Instruction, val define: Boolean = false) : Instruction()  {
    override fun exec(env: Env) = sequence<Instruction> {
        if(define) {
            env.new(name, yieldAllLR(instruction.exec(env)))
        }else env[name] = yieldAllLR(instruction.exec(env))
        
        yield(this@SetVar)
    }.iterator()
}