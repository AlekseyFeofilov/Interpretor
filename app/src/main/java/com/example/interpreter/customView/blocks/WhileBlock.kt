package com.example.interpreter.customView.blocks

import android.content.Context
import android.util.AttributeSet
import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.ioTypes.*

class WhileBlock @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BlockView(context, attrs, defStyleAttr) {
    /*override fun compile() {
        super.compile()
    }*/
    
    init {
        addInput(InputBoolean(IO.Name.Condition, this, "condition: Boolean", isDefault = false))
        addOutput(OutputFunction(IO.Name.True, this, "action"))
        setHeader("While", "#6B8C9E")
    }
}