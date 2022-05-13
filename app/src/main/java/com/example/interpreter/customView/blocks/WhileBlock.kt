package com.example.interpreter.customView.blocks

import android.content.Context
import android.util.AttributeSet
import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.ioInterfaces.ioTypes.*

class WhileBlock @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BlockView(context, attrs, defStyleAttr) {
    override fun compile() {
        super.compile()
    }
    
    init {
        addInput(InputBoolean("condition: Boolean", this, isDefault = false))
        addInput(InputFunction("action", this))
        setHeader("While", "#6B8C9E")
    }
}