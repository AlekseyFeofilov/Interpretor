package com.example.interpreter.customView.blocks

import android.content.Context
import android.util.AttributeSet
import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.ioTypes.InputString
import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.Env
import com.example.interpreter.vm.instruction.*
import com.example.interpreter.vm.instruction.Number
import kotlin.reflect.KClass
import kotlin.text.String

class InitializationBlock @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BlockView(context, attrs, defStyleAttr) {
    override fun compile(compiler: Compiler): List<Instruction> {
        super.compile(compiler)
        return initVariables(compiler)
    }
    
    private fun toBool(string: String): Boolean{
        return when{
            string.matches("""\s*true\s*""".toRegex()) -> true
            string.matches("""\s*false\s*""".toRegex()) -> false
            else -> {
                val value = """\s*(\S*)""".toRegex().find(string)
                throw Error("${value!!.groups[1]!!.value} isn't boolean value")
            }
        }
    }
    
    private fun initVariables(compiler: Compiler): MutableList<Instruction> {
        val initializationList = mutableListOf<Instruction>()
        
        inputs.forEach { pair ->
            if(pair.first !is InputString || pair.first.getValue() == null) return@forEach
            val value = (pair.first as InputString).default
            
            if (value != null) {
                val initializations = value.split(",")
                
                initializations.forEach { initialization ->
                    var instruction: Instruction = when (pair.first.name) {
                        IO.Name.Double -> Number(compiler)
                        IO.Name.Int -> Int(compiler, 0)
                        IO.Name.String -> String(compiler, "")
                        IO.Name.Array -> Object(compiler)
                        else -> Bool(compiler, true)
                    }
                    
                    val assignment = """^\s*([_A-Za-z][_A-Za-z\d]*)\s*(=\s*)?(.+)?$""".toRegex()
                            .find(initialization)
                    
                    if (initialization != assignment?.groups?.get(0)?.value) throw Error("incorrect expression $initialization")
                    compiler.defineVar(assignment.groups[1]!!.value, instruction::class)
                    val debug = compiler.checkVar(assignment.groups[1]!!.value)
                    
                    if (assignment.groups[2]?.value.let { it != null && it != "" }) {
                        if (assignment.groups[3]?.value.let { it != null && it != "" }) {
                            instruction = when (pair.first.name) {
                                IO.Name.Double, IO.Name.Int -> Math(compiler, assignment.groups[3]!!.value)
                                IO.Name.String -> String(compiler, assignment.groups[3]!!.value)
                                IO.Name.Array -> throw Error("you should use Set Array")
                                else -> Bool(compiler, toBool(assignment.groups[3]!!.value))
                            }
                        } else throw Error("incorrect expression $initialization")
                    }
                    
                    initializationList.add(SetVar(compiler, assignment.groups[1]!!.value, instruction, true))
                }
            }
        }
        
        return initializationList
    }
    
    init {
        addInput(InputString(IO.Name.Int, this, "Int:",true, isLink = false))
        addInput(InputString(IO.Name.Double, this, "Double:",true, isLink = false))
        addInput(InputString(IO.Name.String, this, "String:",true, isLink = false))
        addInput(InputString(IO.Name.Boolean, this, "Boolean:", true, isLink = false))
        addInput(InputString(IO.Name.Array, this, "Array:", true, isLink = false))
        
        setHeader("Init", "#8281B1")
    }
}