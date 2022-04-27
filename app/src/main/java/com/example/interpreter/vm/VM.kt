package com.example.interpreter.vm

import android.util.Log
import com.example.interpreter.vm.instruction.Nop
import com.example.interpreter.vm.instruction.Number
import com.example.interpreter.vm.instruction.Math
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.concurrent.thread

class VM {
    lateinit var block: Executor
    
    constructor(instructions: List<Instruction>){
        this.block = Executor(Env(), instructions)
    }
    
    constructor(block: Executor){
        this.block = block
    }
    
    constructor(@Suppress("UNUSED_PARAMETER") Data: String){
        TODO("write JSON parser/stringifier for instruction")
        @Suppress("UNREACHABLE_CODE")
        this.block = Json.decodeFromString<Executor>(Data)
    }
    
    constructor(){ // For tests
//        Log.i("VM", this.block.toString())
//        Log.i("VM", this.Tree[0].Type ?: "")
//        Log.i("VM", this.Tree[1].Type ?: "")
//        Log.i("VM", this.Tree[2].Type ?: "")
//        Log.i("VM", Json.encodeToString(this.Tree))
        
        this.block = Executor(Env(), listOf(/*Math(""),*/ Number(), Nop()))
        start()
    }
    
    fun start(){
        _DEBUGGER()
    }
    
    private fun _DEBUGGER() = thread(
        start = true,
        isDaemon = false,
        contextClassLoader = null,
        name = "VM"
    ) {
        val vm = _VM().iterator()
        
        while(true){
            if(vm.hasNext()) {
                vm.next()
            }
            
            Log.i("VM", "debugCaller")
            Thread.sleep(500)
        }
    }
    
    private fun _VM() = sequence {
//        while(true) {
//            with()
//            Log.i("VM", "inst")
//            yield(null)
//        }
        
        yieldAll(block.exec())
    }
}

suspend fun kotlin.sequences.SequenceScope<Instruction>.yieldAllLR(iterator: Iterator<Instruction>): Instruction {
    var last: Instruction = Nop()
    
    while (iterator.hasNext()){
        last = iterator.next()
        yield(last)
    }
    
    return last
}