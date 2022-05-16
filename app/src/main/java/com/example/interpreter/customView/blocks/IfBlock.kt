package com.example.interpreter.customView.blocks

import android.content.Context
import android.util.AttributeSet
import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.ioTypes.InputBoolean
import com.example.interpreter.ioInterfaces.ioTypes.InputFunction
import com.example.interpreter.vm.instruction.Instruction

class IfBlock @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BlockView(context, attrs, defStyleAttr) {
/*
    override fun compile(): List<Instruction> {
        super.compile()
    }
*/
    init {
        addInput(InputBoolean(IO.Name.Condition,this, "Condition", true, false))
        addInput(InputFunction(IO.Name.True, this, "If condition = true"))
        addInput(InputFunction(IO.Name.False, this, "Else"))
        
        setHeader("If", "#0B6E4F")
    }
}