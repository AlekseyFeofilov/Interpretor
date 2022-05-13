package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Env
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.Int

@Serializable
open class Int(val value: Int = 0) : Instruction() {
    @SerialName("_isBasic")
    override val isBasic: Boolean = true
    
    override fun exec(env: Env) = sequence<Instruction> { yield(this@Int) }.iterator()
    override fun toNumber(): Double = value.toDouble()
    override fun toString(): kotlin.String = value.toString()
}