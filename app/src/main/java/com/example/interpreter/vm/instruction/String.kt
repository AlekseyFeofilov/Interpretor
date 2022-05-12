package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Env
import com.example.interpreter.vm.awaitLR
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.lang.Error

@Suppress("FunctionName", "RemoveRedundantQualifierName")
@Serializable
class String : Instruction {
    @SerialName("_isBasic")
    override val isBasic: Boolean = true
    
    val value: @Contextual Any
    
    override fun exec(env: Env) = sequence<Instruction> { yield(this@String) }.iterator()
    override fun toNumber(): Double = _toString(value).toDouble()
    override fun toString(): kotlin.String = _toString(value)
    
    private fun _toString(value: Any): kotlin.String{
        if(value is kotlin.String) return value
        if(value is Bool) return if(value.toBool()) "True" else "False"
        if(value is Number) return value.toNumber().toString()
        if(value is Object) return value.toString()
        if(value is Register) return _toString(awaitLR(value.exec()))
        
        throw Error("Runtime Error 'to string' instruction not entry")
    }
    
    constructor(value: kotlin.String) : super() {
        this.value = value
    }
    
    constructor(value: Instruction) : super(){
        this.value = value
    }
}