package com.example.interpreter.vm

import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.Input
import com.example.interpreter.ioInterfaces.Output
import com.example.interpreter.vm.instruction.Bool
import com.example.interpreter.vm.instruction.Instruction
import com.example.interpreter.vm.instruction.Nop
import com.example.interpreter.vm.instruction.Register
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import kotlin.reflect.KClass

@Serializable(with = Compiler.Serializer::class)
@Suppress("RemoveRedundantQualifierName", "ConvertSecondaryConstructorToPrimary")
open class Compiler {
    class Serializer : KSerializer<Compiler> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor(Compiler::class.qualifiedName ?: "*EMPTY qualifiedName*") {}
        
        override fun serialize(encoder: Encoder, value: Compiler) {}
        override fun deserialize(decoder: Decoder): Compiler {
            return FCompiler()
        }
    }
    
//    private var definedVars = hashMapOf<kotlin.String, KClass<Instruction>>()
    private var stack: ArrayDeque<Pair<Env, MutableList<Instruction>>> = ArrayDeque()
    val blockView: BlockView?
    var currBlockView: BlockView? = null
    
    class FCompiler() : Compiler(null){
        override fun defineVar(name: kotlin.String, clazz: KClass<Instruction>){
        
        }
        
        override fun checkVar(name: kotlin.String): KClass<Instruction> {
            return Instruction::class
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
        val last = stack.lastOrNull() ?: throw Error("compiler stack corrupted")
        if(last.first.define(name).let { it != null && it != clazz }) throw Error("Redefine var to new type")
        
        last.first.define(name, clazz)
    }
    
    open fun checkVar(name: kotlin.String): KClass<Instruction>? {
        val last = stack.lastOrNull() ?: throw Error("compiler stack corrupted")
        
        return last.first.define(name)
    }
    
    operator fun get(name: IO.Name): Register{
        val bv = currBlockView ?: throw Error("compiler context corrupted")
        
        val inNext = bv.getInputsHash()[name]
        
        if(inNext is Input){
//            inNext.parent.
        }
        
        if(inNext is List<*>){
        
        }
        
        throw Error("inNext not input or List<Input>")
//        return Register(this, Nop(this))
    }
//    operator fun set(name: kotlin.String, value: Any) {}
    
    constructor(value: BlockView?) {
//        if(value == null) throw Error("TODO")
        blockView = value
    }
    
    fun compile(): Executor{
        var currBlockView = blockView ?: throw Error("Fake compiler not call compile method")
        
        while(true) {
            val hashOut = currBlockView.getOutputsHash()
            val last = stack.lastOrNull() ?: throw Error("compiler stack corrupted")
            
            last.second.addAll(currBlockView.compile(this))
            
            val outNext = hashOut[IO.Name.To] ?: break
            val inputNext = currBlockView.getLinkOutput(outNext).getOrNull(0) ?: break
            
            currBlockView = inputNext.parent.view as BlockView
        }
        
        return Executor(Env(), listOf())
    }
}