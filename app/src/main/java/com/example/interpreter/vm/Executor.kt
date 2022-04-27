package com.example.interpreter.vm

class Executor(private val env: Env, private val tree: List<Instruction>) {
    
    fun exec() = sequence {
        for(i in tree) {
            yieldAllLR(i.exec(env))
        }
    }
}