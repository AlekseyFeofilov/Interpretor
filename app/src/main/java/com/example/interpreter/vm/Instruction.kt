package com.example.interpreter.vm

import java.lang.Error

//import kotlinx.serialization.Serializable

//@Serializable
abstract class Instruction {
    open val type: String = javaClass.simpleName
    open val isBasic: Boolean = false
    open var blocks: List<Executor>? = null
    
    val TAG: String = "VM_INSTRUCTION[$type]"
    
    abstract fun exec(env: Env): Iterator<Instruction>
    
    open fun toNumber(): Double = throw Error("Is not a number")
    override fun toString(): String = throw Error("Is not a string")
    
    constructor()
    constructor(blocks: List<Executor>){
        this.blocks = blocks
    }
}