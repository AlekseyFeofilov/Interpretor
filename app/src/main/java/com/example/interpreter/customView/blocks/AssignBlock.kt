package com.example.interpreter.customView.blocks

import android.content.Context
import android.util.AttributeSet
import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.ioTypes.InputString
import com.example.interpreter.vm.instruction.Instruction
import com.example.interpreter.vm.Compiler

class AssignBlock @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BlockView(context, attrs, defStyleAttr) {
    /*override fun compile(compiler: Compiler): List<Instruction> {
        super.compile(compiler)
        

    }*/
    init {
        addInput(InputString(IO.Name.Variable, this, "Variable:", isLink = false))
        addInput(InputString(IO.Name.Value, this, "Value:", isLink = false))
        
        setHeader("Assign", "#DB5764")
    }
}