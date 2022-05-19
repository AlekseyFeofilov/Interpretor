package com.example.interpreter.customView.blocks

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.UiThread
import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.Output
import com.example.interpreter.ioInterfaces.ioTypes.InputBoolean
import com.example.interpreter.ioInterfaces.ioTypes.InputString
import com.example.interpreter.ioInterfaces.ioTypes.OutputBoolean
import com.example.interpreter.ioInterfaces.ioTypes.OutputString
import com.example.interpreter.vm.instruction.Instruction
import com.example.interpreter.vm.Compiler

class BoolBlock @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BlockView(context, attrs, defStyleAttr) {
    override fun compile(compiler: Compiler): List<Instruction> {
//        super.compile(compiler)
        
        return (inputs[1].second.parent.view as BlockView).compile(compiler)
    }
    
    init {
        addInput(InputBoolean(IO.Name.Boolean, this, "val: "))
        addOutput(OutputBoolean(IO.Name.Second, this, "val: "))
        
        setHeader("Bool", "#6CD4FF")
    }
}