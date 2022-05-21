package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.*
import kotlinx.serialization.Serializable

@Serializable
class While : Instruction {
    override fun exec(env: Env) = sequence {
        yield(this@While)
        
        while (true) {
            val ret = yieldAllLR(
                blocks?.getOrNull(0)?.exec() ?: throw Error("Runtime error 'While[0]' not condition")
            )
    
            fun _unRegister(value: Instruction, env: Env): Instruction{
                if(value is Register) return _unRegister(awaitLR(value.exec(env)), env)
        
                return value
            }
            
            val bool = _unRegister(ret, env)
            
            if (bool is Bool && bool.toBool()) {
                yieldAllLR(
                    blocks?.getOrNull(1)?.exec() ?: throw Error("Runtime error 'While[1]' not body")
                )
            } else break
        }
    }.iterator()
    
    constructor(compiler: Compiler, block: List<Executor>) : super(compiler, block) { this.blocks = block }
}