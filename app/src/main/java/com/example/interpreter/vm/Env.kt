package com.example.interpreter.vm

import com.example.interpreter.vm.instruction.Instruction
import com.example.interpreter.vm.instruction.Object
import kotlinx.serialization.descriptors.SerialKind
import java.lang.Error

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class Env {
    @Transient
    val register = hashMapOf<Instruction, Instruction>()
    val vars = hashMapOf<String, Instruction>()
    var env: Env? = null
    
    constructor()
    
    constructor(Env: Env) {
        this.env = Env
    }
    
    private fun recursiveSearchEnv(item: String): Env{
        var parent: Env = this
    
        while(!parent.vars.containsKey(item)){
            if(parent.env != null) {
                parent = parent.env!!
            }else throw Error("Runtime Error (Env recursiveSearchEnv var [${item}] is not defined)")
        }
        
        return parent
    }
    
    operator fun get(item: String): Instruction = recursiveSearchEnv(item).vars[item]!!
    operator fun set(item: String, value: Instruction){ recursiveSearchEnv(item).vars[item] = value }
    
    fun new(item: String, value: Instruction){
        if(!vars.containsKey(item)) {
            vars[item] = value
        }else throw Error("Runtime Error (Env new var [${item}] is already defined)")
    }
    
    private fun regRecursiveSearchEnv(item: Instruction): Env?{
        var parent: Env = this
    
        while(!parent.register.containsKey(item)){
            if(parent.env != null) {
                parent = parent.env!!
            }else return null
        }
    
        return parent
    }
    
    fun reg(item: Instruction): Instruction?{
        val currEnv = regRecursiveSearchEnv(item) ?: return null
        
        return currEnv.register[item]
    }
    
    fun reg(item: Instruction, value: Instruction){
        if(value !is Object) return
        register[item] = value
    }
}