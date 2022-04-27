package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Env
import com.example.interpreter.vm.Instruction

class Nop() : Instruction() {
    override fun exec(env: Env) = sequence<Instruction> { yield(this@Nop) }.iterator()
}