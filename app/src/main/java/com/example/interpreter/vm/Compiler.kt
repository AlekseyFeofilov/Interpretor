package com.example.interpreter.vm

import com.example.interpreter.customView.blockView.BlockView
import kotlinx.serialization.Serializable

@Serializable
@Suppress("RemoveRedundantQualifierName", "ConvertSecondaryConstructorToPrimary")
open class Compiler {
//    val
    
    class FCompiler() : Compiler(null){
        // todo: fake fun
    }
    
    fun push(){
        //Executor - localVar = true
    }
    
    fun pop(): Executor{
        return Executor(Env(), listOf())
    }
    @Suppress("UNUSED_PARAMETER")
    fun defineVar(name: kotlin.String){
    
    }
    @Suppress("UNUSED_PARAMETER")
    fun checkVar(name: kotlin.String): kotlin.Boolean{
        return true
    }
    
    constructor(value: BlockView?) {
        if(value == null) throw Error("TODO")
    }
    
    fun compile(): Executor{
        return Executor(Env(), listOf())
    }
}