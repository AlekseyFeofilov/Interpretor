package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Env
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
open class Number(val value: Double = 0.0) : Instruction() {
    @SerialName("_isBasic")
    override val isBasic: Boolean = true
    
    override fun exec(env: Env) = sequence<Instruction> { yield(this@Number) }.iterator()
    override fun toNumber(): Double = value
    override fun toString(): kotlin.String = value.toString()
}