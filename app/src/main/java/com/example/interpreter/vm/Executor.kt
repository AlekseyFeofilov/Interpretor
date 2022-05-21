package com.example.interpreter.vm

import com.example.interpreter.vm.instruction.Instruction
import kotlinx.serialization.Serializable

@Suppress("MemberVisibilityCanBePrivate")
@Serializable
class Executor {
    val env: Env
    val tree: MutableList<Instruction>
    val localEnv: Boolean
    
    fun exec() = sequence {
        for(i in tree) {
            env.reg(i, yieldAllLR(i.exec(env)))
        }
        
        if(localEnv) env.clear()
    }.iterator()
    
    constructor(env: Env, tree: List<Instruction>, localEnv: Boolean = false){
        this.env = env
        this.tree = tree.toMutableList()
        this.localEnv = localEnv
    }
}