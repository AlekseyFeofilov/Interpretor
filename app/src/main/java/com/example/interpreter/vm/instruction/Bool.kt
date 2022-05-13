package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Env
import com.example.interpreter.vm.awaitLR
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.lang.Error

@Suppress("RemoveRedundantQualifierName", "FunctionName")
@Serializable
open class Bool : Instruction {
    @SerialName("_isBasic")
    override val isBasic: Boolean = true
    
    @Transient
    private var value: @Contextual Any = false
    
    @SerialName("value")
    val v: kotlin.Boolean
        get() = _toBool(value)
    
    override fun exec(env: Env) = sequence<Instruction> { yield(this@Bool) }.iterator()
    override fun toNumber(): Double = if(_toBool(value)) 1.0 else 0.0
    override fun toString(): kotlin.String = _toBool(value).toString()
    fun toBool(): kotlin.Boolean = _toBool(value)
    
    private fun _toBool(value: Any): kotlin.Boolean{
        if(value is Boolean) return value
        if(value is Number) return value.toNumber() != 0.0
        if(value is String) return value.toString().isNotEmpty()
        if(value is Object) return value.value.values.any()
        if(value is Register) return _toBool(awaitLR(value.exec()))
        
        throw Error("Runtime Error 'to bool' instruction not entry")
    }
    
    constructor(value: kotlin.Boolean) : super() {
        this.value = value
    }
    
    constructor(value: Instruction) : super(){
        this.value = value
    }
}