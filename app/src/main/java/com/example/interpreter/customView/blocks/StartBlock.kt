package com.example.interpreter.customView.blocks

import android.content.Context
import android.util.AttributeSet
import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.ioTypes.OutputFunction
import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.instruction.Instruction

class StartBlock  @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BlockView(context, attrs, defStyleAttr) {
    override fun compile(compiler: Compiler): List<Instruction> {
        super.compile(compiler)
        return listOf()
    }
    
    override fun init() {
        addOutput(OutputFunction(IO.Name.To, this, "To"))
    }
    
    init {
        setHeader("Start", "#B3001B")
    }
}