package com.example.interpreter.ioInterfaces.ioTypes

import android.content.Context
import android.util.AttributeSet
import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.customView.blockView.IOContainer
import com.example.interpreter.ioInterfaces.Input
import com.example.interpreter.ioInterfaces.Output
import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.instruction.Instruction
import com.example.interpreter.vm.instruction.String

class InputString(
    override val name: IO.Name,
    override var parent: IOContainer,
    override val description: kotlin.String = "",
    override val autocomplete: Boolean = false,
    override val isDefault: Boolean = true,
    override val isLink: Boolean = true
) : Input {
    override val type = IO.Type.String
    override val color = "#AB954D"
    var default: kotlin.String? = null
    
    override fun parseValue(value: kotlin.String){
        if(value.isEmpty()) {
            default = null
            return
        }
        
        default = value
    }
    
    override fun clone(): Input {
        return InputString(name, parent, description, autocomplete, isDefault, isLink)
    }
    
    override fun getValue() = default
    
    override fun generateCoupleOutput(): Output {
        return OutputString(IO.Name.Fake, FakeBlock(parent.view.context, this), input = this)
    }
    
    private class FakeBlock @JvmOverloads constructor(
        context: Context, val input: Input, attrs: AttributeSet? = null
    ) : BlockView(context, attrs) {
        override fun init() {
        
        }
        
        override fun compile(compiler: Compiler): List<Instruction> {
            return listOf(
                String(
                    compiler,
                    input.getValue() as String
                )
            )
        }
    }
}