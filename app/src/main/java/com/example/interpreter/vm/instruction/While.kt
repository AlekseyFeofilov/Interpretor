package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.Env
import com.example.interpreter.vm.Executor
import com.example.interpreter.vm.yieldAllLR
import kotlinx.serialization.Serializable

@Serializable
class While : Instruction {
    override fun exec(env: Env) = sequence {
        yield(this@While)
        
        while (true) {
            val ret = yieldAllLR(
                blocks?.getOrNull(0)?.exec() ?: throw Error("Runtime error 'While[0]' not condition")
            )
            
            if (ret is Bool && ret.toBool()) {
                yieldAllLR(
                    blocks?.getOrNull(1)?.exec() ?: throw Error("Runtime error 'While[1]' not body")
                )
            } else break
        }
    }.iterator()
    constructor(compiler: Compiler, block: List<Executor>) : super(compiler, block) { this.blocks = block }
}