package com.example.interpreter.customView.blocks

import android.content.Context
import android.util.AttributeSet
import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.ioTypes.InputString
import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.instruction.*
import com.example.interpreter.vm.instruction.Number

class InputVarBlock @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BlockView(context, attrs, defStyleAttr) {
    override fun compile(compiler: Compiler): List<Instruction> {
        super.compile(compiler)
        return requestVariables(compiler)
    }
    
    private fun toBool(string: String): Boolean {
        return when {
            string.matches("""\s*true\s*""".toRegex()) -> true
            string.matches("""\s*false\s*""".toRegex()) -> false
            else -> {
                val value = """\s*(\S*)""".toRegex().find(string)
                throw Error("${value!!.groups[1]!!.value} isn't boolean value")
            }
        }
    }
    
    @Suppress("UNREACHABLE_CODE")
    private fun requestVariables(compiler: Compiler): MutableList<Instruction> {
        val initializationList = mutableListOf<Instruction>()
        
        inputs.forEach { it ->
            if (it.first !is InputString) return@forEach
            val value = (it.first as InputString).default
            
            if (value != null) {
                val initializations = value.split(",")
                
                initializations.forEach { initialization ->
                    val variable = """^\s*(\S+)\s*$""".toRegex().find(initialization)
                    
                    if (initialization != variable?.groups?.get(0)?.value) throw Error("incorrect expression $initialization")
                    
                    /*initializationList.add(
                        Input(
                            compiler,
                            variable.groups[1]!!.value,
                            when(it.first.name){
                                IO.Name.Int -> com.example.interpreter.vm.instruction.Int::class
                                IO.Name.Double -> Number::class
                                IO.Name.String -> com.example.interpreter.vm.instruction.String::class
                                IO.Name.Boolean -> Bool::class
                                else -> throw Error("internal error in InitVarBlock")
                            }
                        )
                    )*/
                }
            }
        }
        
        return initializationList
    }
    
    init {
        addInput(InputString(IO.Name.Int, this, "Int:", true, isLink = false))
        addInput(InputString(IO.Name.Double, this, "Double:", true, isLink = false))
        addInput(InputString(IO.Name.String, this, "String:", true, isLink = false))
        addInput(InputString(IO.Name.Boolean, this, "Boolean:", true, isLink = false))
        
        setHeader("Input variable", "#8F95D3")
    }
}