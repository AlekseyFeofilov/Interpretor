package com.example.interpreter.vm

import android.util.Log
import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.Input
import com.example.interpreter.ioInterfaces.ioTypes.OutputFunction
import com.example.interpreter.vm.instruction.*
import com.example.interpreter.vm.instruction.Number
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.collections.HashMap
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
    private val stack: ArrayDeque<Pair<Env, MutableList<Instruction>>> = ArrayDeque()
    private val cacheInputs: HashMap<Pair<BlockView, IO.Name>, List<Instruction>> = hashMapOf()
    
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
        val exec = stack.removeLastOrNull() ?: throw Error("compiler stack corrupted")
        
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
    
    operator fun get(name: IO.Name): Any{ /* Register or List<Register> */
        val last = stack.lastOrNull() ?: throw Error("compiler stack corrupted")
        val bv = currBlockView ?: throw Error("compiler context corrupted")
        
        val inNext = bv.getInputsHash()[name] ?: bv.getOutputsHash()[name] ?: throw Error("compiler crash input/output in hashT is null")
    
        fun blockViewCompile(curr: BlockView, name: IO.Name): Instruction {
            if (cacheInputs[Pair(curr, name)] == null) {
                val ret = curr.compile(this)
            
                cacheInputs[Pair(curr, name)] = ret
                last.second.addAll(ret)
            }
        
            val listInstruction = cacheInputs[Pair(curr, name)]!!
            
            return listInstruction.lastOrNull() ?: throw Error("last instruction is null")
        }
        
        if(inNext is OutputFunction){
//            currBlockView = bv.getLinkOutput(inNext).parent.view as BlockView
//                val ret = blockViewCompile(currBlockView!!, name)
//            currBlockView = bv
    
            return Register(this, Nop(this) /* ret */, env = last.first)
        }
        
        if(inNext is Input){
            currBlockView = bv.getLinkInput(inNext).parent.view as BlockView
                val ret = blockViewCompile(currBlockView!!, name)
            currBlockView = bv
            
            return Register(this, ret, env = last.first)
        }
        
        if(inNext is List<*>){
            val listRet = mutableListOf<Register>()
            for(i in inNext) {
                currBlockView = bv.getLinkInput(i as Input).parent.view as BlockView
    
                val ret = blockViewCompile(currBlockView!!, name)
                android.util.Log.i("COMPILER", currBlockView.toString())
    
                listRet.add(Register(this, ret, env = last.first))
            }
            
            currBlockView = bv
            return  listRet
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
        currBlockView = blockView ?: throw Error("Fake compiler not call compile method")
        
        push()
        
        while(true) {
            val hashOut = currBlockView!!.getOutputsHash()
            val last = stack.lastOrNull() ?: throw Error("compiler stack corrupted")
            
            last.second.addAll(currBlockView!!.compile(this))
//            last.second.add(Number(this, 0.0))
            
            val outNext = hashOut[IO.Name.To] ?: break
            val inputNext = currBlockView!!.getLinkOutput(outNext).getOrNull(0) ?: break
            
            currBlockView = inputNext.parent.view as BlockView
        }
        
        return pop().let{ Log.i("Compiler", Json{ prettyPrint = true; serializersModule = Math.module }.encodeToString(it)); it }
    }
}