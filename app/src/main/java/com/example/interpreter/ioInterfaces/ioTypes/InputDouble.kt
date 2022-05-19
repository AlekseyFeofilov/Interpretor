package com.example.interpreter.ioInterfaces.ioTypes

import android.content.Context
import android.util.AttributeSet
import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.customView.blockView.IOContainer
import com.example.interpreter.ioInterfaces.Input
import com.example.interpreter.ioInterfaces.Output
import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.instruction.Number
import com.example.interpreter.vm.instruction.Instruction
import java.lang.Double.parseDouble

class InputDouble(
    override val name: IO.Name,
    override var parent: IOContainer,
    override val description: String = "",
    override val autocomplete: Boolean = false,
    override val isDefault: Boolean = true,
    override val isLink: Boolean = true
) : Input {
    override val type = IO.Type.Double
    override val color = "#80505B"
    var default: Double? = null
    
    override fun parseValue(value: String) {
        if (value.isEmpty()) {
            default = null
            return
        }
        
        default = try {
            parseDouble(value)
        }
        catch (e: java.lang.NumberFormatException){
            0.0
        }
    }
    
    override fun clone(): Input {
        return InputDouble(name, parent, description, autocomplete, isDefault, isLink)
    }
    
    override fun getValue() = default
    
    override fun generateCoupleOutput(): Output {
        return OutputDouble(IO.Name.Fake, FakeBlock(parent.view.context, this), input = this)
    }
    
    private class FakeBlock @JvmOverloads constructor(
        context: Context, val input: Input, attrs: AttributeSet? = null
    ) : BlockView(context, attrs) {
        override fun init() {
        
        }
    
        override fun compile(compiler: Compiler): List<Instruction> {
            return listOf(
                Number(
                compiler,
                    input.getValue() as Double
            ))
        }
    }
    
}