package com.example.interpreter.customView.blocks

import android.content.Context
import android.util.AttributeSet
import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.ioTypes.InputBoolean
import com.example.interpreter.ioInterfaces.ioTypes.OutputAny
import com.example.interpreter.ioInterfaces.ioTypes.OutputBoolean
import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.instruction.Input
import com.example.interpreter.vm.instruction.Instruction

class InputBlock @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BlockView(context, attrs, defStyleAttr) {
    override fun compile(compiler: Compiler): List<Instruction> {
        super.compile(compiler)
        return listOf(Input(compiler))
    }
    
    init {
        addOutput(OutputAny(IO.Name.out, this, "value:"))
        
        setHeader("Input", "#6CD4FF")
    }
}