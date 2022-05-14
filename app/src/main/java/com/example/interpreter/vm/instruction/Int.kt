package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.Env
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
open class Int : Instruction {
    @SerialName("_isBasic")
    override val isBasic: Boolean = true
    
    private val value: kotlin.Int
    
    override fun exec(env: Env) = sequence<Instruction> { yield(this@Int) }.iterator()
    override fun toNumber(): Double = value.toDouble()
    override fun toString(): kotlin.String = value.toString()
    
    constructor(compiler: Compiler, value: kotlin.Int) : super(compiler) { this.value = value }
}