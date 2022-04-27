package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Env
import com.example.interpreter.vm.Instruction

class Switch : Instruction  {
    
    override fun exec(env: Env) = sequence<Instruction> { yield(this@Switch) }.iterator()
    
    constructor() : super() { TODO("NOT READY") }
}