package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Env
import com.example.interpreter.vm.Executor
import java.lang.Error
import kotlin.String
import kotlinx.serialization.Serializable

@Serializable
sealed class Instruction {
//    @SerialName("class_type")
    companion object{
        var _id = 0
    }
    
    open val types: String = javaClass.simpleName
    open val isBasic: Boolean = false
    open val id = _id++
    open var blocks: List<Executor>? = null
    
    val TAG: String = "VM_INSTRUCTION[$types]"
    
    abstract fun exec(env: Env): Iterator<Instruction>
    
    open fun toNumber(): Double = throw Error("Is not a number")
    override fun toString(): String = throw Error("Is not a string")
    
    constructor()
    constructor(blocks: List<Executor>){
        this.blocks = blocks
    }
}