package com.example.interpreter.ioInterfaces.ioTypes

import android.content.Context
import android.util.AttributeSet
import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.customView.blockView.IOContainer
import com.example.interpreter.ioInterfaces.Input
import com.example.interpreter.ioInterfaces.Output
import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.instruction.Int
import com.example.interpreter.vm.instruction.Instruction

class InputInt(
    override val name: IO.Name,
    override var parent: IOContainer,
    override val description: String = "",
    override val autocomplete: Boolean = false,
    override val isDefault: Boolean = true,
    override val isLink: Boolean = true
) : Input {
    override val type = IO.Type.Double
    override val color = "#80505B"
    var default: kotlin.Int? = null
    
    override fun parseValue(value: String) {
        if (value.isEmpty()) {
            default = null
            return
        }
        
        default = try {
            value.toInt()
        } catch (e: NumberFormatException) {
            0
        }
    }
    
    override fun clone(): Input {
        return InputDouble(name, parent, description, autocomplete, isDefault, isLink)
    }
    
    override fun getValue() = default
    
    override fun generateCoupleOutput(): Output {
        return OutputDouble(IO.Name.Fake, FakeBlock(parent.view.context, this))
    }
    
    private class FakeBlock @JvmOverloads constructor(
        context: Context, val input: InputInt, attrs: AttributeSet? = null
    ) : BlockView(context, attrs) {
        override fun init() {
        
        }
        
        override fun compile(compiler: Compiler): List<Instruction> {
            return listOf(
                Int(
                    compiler,
                    input.getValue() ?: throw Error("argument is null")
                )
            )
        }
    }
}