package com.example.interpreter.vm

import android.util.Log
import com.example.interpreter.WorkspaceFragment
import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.Input
import com.example.interpreter.ioInterfaces.ioTypes.*
import com.example.interpreter.vm.instruction.*
import com.example.interpreter.vm.instruction.Int
import com.example.interpreter.vm.instruction.Number
import com.example.interpreter.vm.instruction.String
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
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
    @Transient
    var context: WorkspaceFragment? = null
    
    class FCompiler() : Compiler(null, null){
        override fun defineVar(name: kotlin.String, clazz: KClass<out Instruction>){
        
        }
        
        override fun checkVar(name: kotlin.String): KClass<Instruction> {
            return Instruction::class
        }
    }
    
    fun push(){
        stack.addLast(Pair(Env(stack.lastOrNull()?.first), mutableListOf()))
    }
    
    fun pop(localVar: kotlin.Boolean = false): Executor{
        val exec = stack.removeLastOrNull() ?: throw Error("compiler stack corrupted")
        
        return Executor(exec.first, exec.second, localVar)
    }
    
    open fun defineVar(name: kotlin.String, clazz: KClass<out Instruction>){
        val last = stack.lastOrNull() ?: throw Error("compiler stack corrupted")
        if(last.first.define(name).let { it != null && it != clazz }) throw Error("Redefine var to new type")
        
        last.first.define(name, clazz)
    }
    
    open fun checkVar(name: kotlin.String): KClass<out Instruction>? {
        val last = stack.lastOrNull() ?: throw Error("compiler stack corrupted")
        
        return last.first.define(name)
    }
    
    private fun typeCast(value: Instruction, inp: Input): Instruction{
        return when(inp){
            is InputBoolean -> if(value !is Bool) Bool(this, value) else value
            is InputDouble -> if(value !is Number) Number(this, value) else value
            is InputString -> if(value !is String) String(this, value) else value
            is InputInt -> if(value !is String) Int(this, value) else value
            
            else -> { throw Error("compiler auto cast error") }
        }
    }
    
    operator fun get(name: IO.Name): Any{ /* Register or List<Register> */
        val last = stack.lastOrNull() ?: throw Error("compiler stack corrupted")
        val bv = currBlockView ?: throw Error("compiler context corrupted")
        
        val inNext = bv.getInputsHash()[name] ?: throw Error("compiler crash input/output in hashT is null")
    
        fun blockViewCompile(curr: BlockView, name: IO.Name): Instruction {
            if (cacheInputs[Pair(curr, name)] == null) {
                val ret = curr.compile(this)
                
                cacheInputs[Pair(curr, name)] = ret
                last.second.addAll(ret)
            }
        
            val listInstruction = cacheInputs[Pair(curr, name)]!!
            
            return listInstruction.lastOrNull() ?: throw Error("last instruction is null")
        }
        
        //todo: compile function input
        
        if(inNext is Input){
            currBlockView = bv.getLinkInput(inNext).parent.view as BlockView
                val ret = blockViewCompile(currBlockView!!, name)
            currBlockView = bv
            
            return Register(this, ret, name.toString(), env = last.first)
        }
        
        if(inNext is List<*>){
            val listRet = mutableListOf<Register>()
            for(i in inNext) {
                currBlockView = bv.getLinkInput(i as Input).parent.view as BlockView
    
                val ret = blockViewCompile(currBlockView!!, name)
                android.util.Log.i("COMPILER", currBlockView.toString())
    
                listRet.add(Register(this, ret, name.toString(), env = last.first))
            }
            
            currBlockView = bv
            return  listRet
        }
        
        throw Error("inNext not input or List<Input>")
//        return Register(this, Nop(this))
    }
//    operator fun set(name: kotlin.String, value: Any) {}
    
    constructor(value: BlockView?, context: WorkspaceFragment?) {
        this.context = context
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