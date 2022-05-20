package com.example.interpreter.customView.blocks

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.app.AppCompatActivity
import com.example.interpreter.R
import com.example.interpreter.WorkspaceFragment
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
    
    //todo: remove brackets and add color
    private fun stringWithoutSpaces(string: String): String? {
        return """\s*(\S+)\s*""".toRegex().find(string)?.groups?.get(1)?.value
    }
    
    private fun printAll(compiler: Compiler): List<Instruction> {
        val prints = mutableListOf<Instruction>()
        val compiledInput = (compiler[IO.Name.Print] as List<*>).iterator()
        
        inputs.forEach { pair ->
            if (
                pair.first !is InputAny ||
                (!isInputAvailable(pair.first) && pair.first.getValue() == null)
            ) {
                return@forEach
            }
            
            if (isInputAvailable(pair.first)) {
                val print = compiledInput.next() as Register
                prints.add(Print(compiler, print))
                endln(compiler)
                return@forEach
            }
            
            (pair.first.getValue() as String)
                .split("""(?<!\\),""".toRegex())
                .forEach {
                    if (stringWithoutSpaces(it) != null) {
                        prints.add(print(compiler, it))
                    }
                }
            endln(compiler)
        }
        
        return prints
    }
    
    private fun endln(compiler: Compiler) = Print(compiler, String(compiler, ""), true)
    
    private fun print(compiler: Compiler, value: String): Print {
        return when {
            value.matches("""^\s*"[^"]*"\s*$""".toRegex()) -> {
                Print(compiler, String(compiler, value))
            }
            compiler.checkVar(stringWithoutSpaces(value)!!) != null -> {
                Print(compiler, GetVar(compiler, stringWithoutSpaces(value)!!))
            }
            else -> {
                throw Error("variable ${stringWithoutSpaces(value)} isn't exist")
            }
        }
    }
    
    init {
        addInput(InputAny(IO.Name.Print, this, "println", true))
        setHeader("Print", "#FFC857")
    }
}