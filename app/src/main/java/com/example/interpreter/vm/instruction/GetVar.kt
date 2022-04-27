package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Env
import com.example.interpreter.vm.Instruction

class GetVar(val name: kotlin.String) : Instruction()   {
    override fun exec(env: Env) = sequence<Instruction> {
        yield(env[name])
    }.iterator()
}