package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Env
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
open class Object(val value: HashMap<kotlin.String, Instruction> = hashMapOf()) : Instruction() {
    @SerialName("_isBasic")
    override val isBasic: Boolean = true
    
    override fun exec(env: Env) = sequence<Instruction> { yield(this@Object) }.iterator()
    override fun toString(): kotlin.String = value.toString()
    
    operator fun get(item: kotlin.String): Instruction? { return value[item] }
    operator fun set(item: kotlin.String, setVal: Instruction) { value[item] = setVal }
}