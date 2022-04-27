package com.example.interpreter.vm

//import kotlinx.serialization.Serializable

//@Serializable
abstract class Instruction {
    open val type: String = javaClass.simpleName
    open val isBasic: Boolean = false
    open var blocks: List<Executor>? = null
    
    val TAG: String = "VM_INSTRUCTION[$type]"
    
    abstract fun exec(env: Env): Iterator<Instruction>
    
    constructor()
    constructor(blocks: List<Executor>){
        this.blocks = blocks
    }
}