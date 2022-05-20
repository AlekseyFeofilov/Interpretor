package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.*
import kotlinx.serialization.Serializable

@Serializable
class Function : Instruction  {
    override fun exec(env: Env) = sequence<Instruction> {
        yieldAllLR(blocks?.getOrNull(0)?.exec() ?: throw Error("Runtime error 'if[0]' not condition"))
    }.iterator()
    
    constructor(compiler: Compiler, block: Executor) : super(compiler, listOf(block))
}