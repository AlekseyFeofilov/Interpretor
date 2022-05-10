package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Env
import kotlinx.serialization.Serializable

@Serializable
class If : Instruction {
    
    override fun exec(env: Env) = sequence<Instruction> { yield(this@If) }.iterator()
    
    constructor() : super() { TODO("NOT READY") }
}