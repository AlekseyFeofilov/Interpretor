package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.Env
import com.example.interpreter.vm.yieldAllLR
import kotlinx.serialization.Serializable

@Serializable
class While : Instruction {
    //override fun exec(env: Env) = sequence<Instruction> { yield(this@While) }.iterator()
    
    override fun exec(env: Env) = sequence<Instruction> {
//        while(true){
//            val da: Instruction = yieldAllLR(blocks!![0].exec())
//
//            if(da is Object){
//                val da1 = da["out"]
//                if(da1 is Bool){
//                    val da2: Boolean = da1.value
//                    if(!da2) break
//                }
//            }
//
//            yieldAll(blocks!![1].exec())
//        }

        yield(this@While)
    }.iterator()
    
    constructor(compiler: Compiler) : super(compiler) { TODO("NOT READY") }
}