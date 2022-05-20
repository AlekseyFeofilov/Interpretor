package com.example.interpreter.ioInterfaces.ioTypes

import android.content.Context
import android.util.AttributeSet
import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.customView.blockView.IOContainer
import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.Input
import com.example.interpreter.ioInterfaces.Output
import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.instruction.Instruction
import com.example.interpreter.vm.instruction.Object

class InputObject (
    override val name: IO.Name,
    override var parent: IOContainer,
    override val description: String = "",
    override val autocomplete: Boolean = false,
    override val isLink: Boolean = true
) : Input {
    override val type = IO.Type.Object
    override val isDefault = false
    override val color = "#732C2C"
    var default: Object? = null
    
    override fun parseValue(value: String) { }
    
    override fun clone(): Input {
        return InputObject(name, parent, description, autocomplete, isLink)
    }
    
    override fun getValue() = default
    
    override fun generateCoupleOutput(): Output {
        return OutputObject(IO.Name.Fake, FakeBlock(parent.view.context))
    }
    
    private class FakeBlock @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null
    ) : BlockView(context, attrs) {
        override fun init() {
        
        }
        
        override fun compile(compiler: Compiler): List<Instruction> {
            return listOf()
        }
    }
}