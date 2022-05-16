package com.example.interpreter.vm

import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.vm.instruction.Instruction
import com.example.interpreter.vm.instruction.Nop
import com.example.interpreter.vm.instruction.Register
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.reflect.KClass

@Serializable
@Suppress("RemoveRedundantQualifierName", "ConvertSecondaryConstructorToPrimary")
open class Compiler {
    @Transient
    var definedVars = hashMapOf<kotlin.String, KClass<Instruction>>()
    @Transient
    var stack: ArrayDeque<Pair<Env, MutableList<Instruction>>> = ArrayDeque()
    
    class FCompiler() : Compiler(null){
        override fun defineVar(name: kotlin.String, clazz: KClass<Instruction>){
        
        }
        
        override fun checkVar(name: kotlin.String): kotlin.Boolean{
            return true
        }
    }
    
    fun push(){
        stack.addLast(Pair(Env(stack.lastOrNull()?.first), mutableListOf()))
    }
    
    fun pop(localVar: kotlin.Boolean = true): Executor{
        val exec = stack.lastOrNull() ?: throw Error("compiler stack corrupted")
        
        return Executor(exec.first, exec.second, localVar)
    }
    
    open fun defineVar(name: kotlin.String, clazz: KClass<Instruction>){
        definedVars[name] = clazz
    }
    
    open fun checkVar(name: kotlin.String): kotlin.Boolean{
        return definedVars.containsKey(name)
    }
    
    operator fun get(name: kotlin.String): Register{ return Register(this, Nop(this)) }
    operator fun set(name: kotlin.String, value: Any) {}
    
    constructor(value: BlockView?) {
        if(value == null) throw Error("TODO")
    }
    
    fun compile(): Executor{
        // TODO: in works
        return Executor(Env(), listOf())
    }
}