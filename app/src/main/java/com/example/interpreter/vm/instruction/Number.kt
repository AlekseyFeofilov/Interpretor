package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Env
import com.example.interpreter.vm.Instruction

open class Number(val value: Double = 0.0) : Instruction() {
    override val isBasic: Boolean = true
    
    override fun exec(env: Env) = sequence<Instruction> { yield(this@Number) }.iterator()
    override fun toNumber(): Double = value
    override fun toString(): kotlin.String = value.toString()
}