package com.example.interpreter.vm

import com.example.interpreter.vm.instruction.Instruction
import kotlinx.serialization.Serializable

@Serializable
class Executor(val env: Env, val tree: List<Instruction>) {
    
    fun exec() = sequence {
        for(i in tree) {
            env.reg(i, yieldAllLR(i.exec(env)))
        }
    }.iterator()
}