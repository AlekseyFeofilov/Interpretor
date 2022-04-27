package com.example.interpreter.vm

import kotlinx.serialization.descriptors.SerialKind
import java.lang.Error

//import kotlinx.serialization.Serializable

//@Serializable
class Env {
    val vars: HashMap<String, Instruction> = hashMapOf<String, Instruction>()
    var env: Env? = null
    
    constructor()
    
    constructor(Env: Env){
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
}