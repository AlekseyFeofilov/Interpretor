package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Env
import com.example.interpreter.vm.Executor
import com.example.interpreter.vm.yieldAllLR
import kotlinx.serialization.Serializable

@Serializable
class If(private val block: List<Executor>) : Instruction(block) {
    override fun exec(env: Env) = sequence<Instruction> {
        yield(this@If)
        
        val ret = yieldAllLR(blocks?.getOrNull(0)?.exec() ?: throw Error("Runtime error 'if[0]' not condition"))
        
        if(ret is Bool && ret.toBool()){
            yieldAllLR(blocks?.getOrNull(1)?.exec() ?: throw Error("Runtime error 'if[1]' not body"))
        }else yieldAllLR(blocks?.getOrNull(2)?.exec() ?: throw Error("Runtime error 'if[2]' not else"))
    }.iterator()
}