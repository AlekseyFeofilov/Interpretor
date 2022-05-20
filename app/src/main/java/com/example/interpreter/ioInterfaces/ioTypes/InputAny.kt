package com.example.interpreter.ioInterfaces.ioTypes

import android.content.Context
import android.util.AttributeSet
import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.customView.blockView.IOContainer
import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.Input
import com.example.interpreter.ioInterfaces.Output
import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.instruction.Bool
import com.example.interpreter.vm.instruction.Instruction
import com.example.interpreter.vm.instruction.String

class InputAny(
    override val name: IO.Name,
    override var parent: IOContainer,
    override val description: kotlin.String = "",
    override val autocomplete: Boolean = false,
    override val isDefault: Boolean = true,
    override val isLink: Boolean = true
) : Input {
    override val type = IO.Type.Any
    override val color = "#FFFFFF"
    var default: kotlin.String? = null
    
    override fun parseValue(value: kotlin.String) {
        default = value.ifEmpty { null }
    }
    
    override fun clone(): Input {
        return InputAny(name, parent, description, autocomplete, isDefault, isLink)
    }
    
    override fun getValue() = default
    
    override fun generateCoupleOutput(): Output {
        return OutputString(IO.Name.Fake, FakeBlock(parent.view.context, this))
    }
    
    private class FakeBlock @JvmOverloads constructor(
        context: Context, val input: InputAny, attrs: AttributeSet? = null
    ) : BlockView(context, attrs) {
        override fun init() {
        
        }
        
        override fun compile(compiler: Compiler): List<Instruction> {
            return listOf(
                String(
                    compiler,
                    input.getValue() ?: throw Error("argument is null")
                )
            )
        }
    }
}