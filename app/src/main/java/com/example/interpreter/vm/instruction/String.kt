package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Env
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class String(val value: kotlin.String) : Instruction() {
    @SerialName("_isBasic")
    override val isBasic: Boolean = true
    
    override fun exec(env: Env) = sequence<Instruction> { yield(this@String) }.iterator()
    override fun toNumber(): Double = value.toDouble()
    override fun toString(): kotlin.String = value
}