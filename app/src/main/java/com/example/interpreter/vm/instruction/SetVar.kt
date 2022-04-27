package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Env
import com.example.interpreter.vm.Instruction

class SetVar(val name: kotlin.String, val instruction: Instruction, val define: Boolean = false) : Instruction()  {
    override fun exec(env: Env) = sequence<Instruction> {
        if(define) {
            env.new(name, instruction)
        }else env[name] = instruction
        
        yield(this@SetVar)
    }.iterator()
}