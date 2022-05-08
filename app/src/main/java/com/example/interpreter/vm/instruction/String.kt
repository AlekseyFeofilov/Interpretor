package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Env
import com.example.interpreter.vm.Instruction

class String(val value: kotlin.String) : Instruction() {
    override val isBasic: Boolean = true
    
    override fun exec(env: Env) = sequence<Instruction> { yield(this@String) }.iterator()
    override fun toNumber(): Double = value.toDouble()
    override fun toString(): kotlin.String = value
}