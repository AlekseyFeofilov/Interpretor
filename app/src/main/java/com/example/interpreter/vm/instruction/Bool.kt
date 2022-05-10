package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Env
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
open class Bool(val value: kotlin.Boolean) : Instruction() {
    @SerialName("_isBasic")
    override val isBasic: Boolean = true
    
    override fun exec(env: Env) = sequence<Instruction> { yield(this@Bool) }.iterator()
    override fun toNumber(): Double = if(value) 1.0 else 0.0
    override fun toString(): kotlin.String = value.toString()
    fun toBool(): kotlin.Boolean = value
}