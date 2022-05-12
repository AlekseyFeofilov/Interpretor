package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Env
import com.example.interpreter.vm.awaitLR
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.lang.Error

@Suppress("RemoveRedundantQualifierName")
@Serializable
open class Bool : Instruction {
    @SerialName("_isBasic")
    override val isBasic: Boolean = true
    
    val value: @Contextual Any
    
    override fun exec(env: Env) = sequence<Instruction> { yield(this@Bool) }.iterator()
    override fun toNumber(): Double = if(toBoolean(value)) 1.0 else 0.0
    override fun toString(): kotlin.String = toBoolean(value).toString()
    fun toBool(): kotlin.Boolean = toBoolean(value)
    
    private fun toBoolean(value: Any): kotlin.Boolean{
        if(value is Boolean) return value
        if(value is Number) return value.value != 0.0
        if(value is String) return value.toString().isNotEmpty()
        if(value is Object) return value.value.values.any()
        if(value is Register) return toBoolean(awaitLR(value.exec()))
        
        throw Error("Runtime Error 'to bool' instruction not entry")
    }
    
    constructor(value: kotlin.Boolean) : super() {
        this.value = value
    }
    
    constructor(value: Instruction) : super(){
        this.value = value
    }
}