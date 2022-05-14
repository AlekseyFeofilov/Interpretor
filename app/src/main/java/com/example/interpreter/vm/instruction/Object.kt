package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Env
import com.example.interpreter.vm.Compiler
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
open class Object : Instruction {
    @SerialName("_isBasic")
    override val isBasic: Boolean = true
    
    private var value: HashMap<kotlin.String, Instruction> = hashMapOf()
    
    val v: HashMap<kotlin.String, Instruction>
        get() = value
    
    override fun exec(env: Env) = sequence<Instruction> { yield(this@Object) }.iterator()
    override fun toString(): kotlin.String = value.toString()
    
    operator fun get(item: kotlin.String): Instruction? { return value[item] }
    operator fun set(item: kotlin.String, setVal: Instruction) { value[item] = setVal }
    
    constructor(compiler: Compiler, value: HashMap<kotlin.String, Instruction>) : super(compiler){
        this.value = value
    }
    
    constructor(compiler: Compiler, vararg value: Pair<kotlin.String, Instruction>) : super(compiler){
        this.value = hashMapOf(*value)
    }
}