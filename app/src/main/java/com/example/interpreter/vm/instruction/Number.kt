package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Env
import com.example.interpreter.vm.awaitLR
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.lang.Error
import kotlin.Number

@Suppress("RemoveRedundantQualifierName", "FunctionName")
@Serializable
open class Number : Instruction {
    @SerialName("_isBasic")
    override val isBasic: Boolean = true
    
    val value: @Contextual Any
    
    override fun exec(env: Env) = sequence<Instruction> { yield(this@Number) }.iterator()
    override fun toNumber(): Double = _toNumber(value)
    override fun toString(): kotlin.String = _toNumber(value).toString()
    
    private fun _toNumber(value: Any): kotlin.Double{
        if(value is kotlin.Double) return value
        if(value is Bool) return if(value.toBool()) 1.0 else 0.0
        if(value is String) return value.toString().toDoubleOrNull() ?: Double.NaN
        if(value is Register) return _toNumber(awaitLR(value.exec()))
        
        throw Error("Runtime Error 'to number' instruction not entry")
    }
    
    constructor(value: kotlin.Double) : super() {
        this.value = value
    }
    
    constructor(value: Instruction) : super(){
        this.value = value
    }
}