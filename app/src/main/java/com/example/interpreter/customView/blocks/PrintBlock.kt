package com.example.interpreter.customView.blocks

import android.content.Context
import android.util.AttributeSet
import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.ioTypes.InputAny
import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.instruction.*
import com.example.interpreter.vm.instruction.Number
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

class PrintBlock @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BlockView(context, attrs, defStyleAttr) {
    override fun compile(compiler: Compiler): List<Instruction> {
        super.compile(compiler)
        return printAll(compiler)
    }
    
    private fun stringWithoutSpaces(string: String): String? {
        return """\s*(\S+)\s*""".toRegex().find(string)?.groups?.get(1)?.value
    }
    
    private fun printAll(compiler: Compiler): List<Instruction> {
        val prints = mutableListOf<Instruction>()
        val compiledInput = (compiler[IO.Name.Print] as List<*>).iterator()
        
        inputs.forEach { pair ->
            if (
                pair.first !is InputAny ||
                (isInputAvailable(pair.first) && pair.first.getValue() == null)
            ) return@forEach
            
            if (isInputAvailable(pair.first)) {
                val print = compiledInput.next() as Register
                prints.add(Print(compiler, print))
                return@forEach
            }
            
            (pair.first.getValue() as String)
                .split("""(?<!\\),""".toRegex())
                .forEach {
                    prints.add(print(compiler, it))
                }
        }
        
        return prints
    }
    
    private fun print(compiler: Compiler, value: String): Print {
        return when {
            value.matches("""^\s*"[^"]*"\s*$""".toRegex()) -> {
                Print(
                    compiler,
                    com.example.interpreter.vm.instruction.String(compiler, value)
                )
            }
            compiler.checkVar(stringWithoutSpaces(value) ?: throw Error("can't print empty string")) != null -> {
                val clazz = compiler.checkVar(stringWithoutSpaces(value)!!)!!
                Print(compiler, getInstructionByClass(compiler, clazz, value))
            }
            else -> {
                throw Error("variable ${stringWithoutSpaces(value)} isn't exist")
            }
        }
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
    
    private fun getInstructionByClass(compiler: Compiler, clazz: KClass<Instruction>, value: String): Instruction{
        return when(clazz){
            Number::class, Int::class -> Math(compiler, value)
            Bool::class -> Bool(compiler, toBool(value))
            else -> {
                val compilerType = Compiler::class.createType()
                val instructionType = Instruction::class.createType()
                val constructor = clazz.constructors.find { it.typeParameters == listOf(compilerType, instructionType) }
                
                return constructor!!.call(compiler, com.example.interpreter.vm.instruction.String(compiler, value))
            }
        }
    }
    
    init {
        addInput(InputAny(IO.Name.Print, this, "println", true))
        setHeader("Print", "#FFC857")
    }
}