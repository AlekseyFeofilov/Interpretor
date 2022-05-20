package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.Env
import com.example.interpreter.vm.awaitLR
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class GetObject : Instruction {
    @Transient
    var obj: Any? = null
    val name: kotlin.String
    
    override fun exec(env: Env) = sequence<Instruction> {
        yield(_toObject(obj!!, env)[name] ?: Nop(Compiler.FCompiler()))
    }.iterator()
    
    private fun _toObject(value: Any, env: Env): Object{
        if(value is Object) return value
        if(value is kotlin.String) return _toObject(env[value], env)
        if(value is String) return _toObject(env[value.toString()], env)
        if(value is Register) return _toObject(awaitLR(value.exec(env)), env)
        
        throw Error("Runtime Error 'to object' instruction not entry")
    }
    
    constructor(compiler: Compiler, obj: Any, name: kotlin.String) : super(compiler) {
        this.obj = obj
        this.name = name
    }
}