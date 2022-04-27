package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Env
import com.example.interpreter.vm.Instruction

class While : Instruction {
    
    override fun exec(env: Env) = sequence<Instruction> { yield(this@While) }.iterator()
    
    constructor() : super() { TODO("NOT READY") }
}