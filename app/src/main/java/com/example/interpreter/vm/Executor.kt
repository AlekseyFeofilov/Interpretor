package com.example.interpreter.vm

import com.example.interpreter.vm.instruction.Instruction
import kotlinx.serialization.Serializable

@Suppress("MemberVisibilityCanBePrivate")
@Serializable
class Executor(val env: Env, val tree: List<Instruction>, val localEnv: Boolean = false) {
    
    fun exec() = sequence {
        for(i in tree) {
            env.reg(i, yieldAllLR(i.exec(env)))
        }
        
        if(localEnv) env.clear()
    }.iterator()
}