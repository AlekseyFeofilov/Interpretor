package com.example.interpreter.customView.blocks

import android.content.Context
import android.util.AttributeSet
import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.ioInterfaces.ioTypes.InputString

class InitializationBlock @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BlockView(context, attrs, defStyleAttr) {
    override fun compile() {
        super.compile()
    }
    init {
        addInput(InputString("Double:", this, true, isLink = false))
        addInput(InputString("String:", this, true, isLink = false))
        addInput(InputString("Boolean:", this, true, isLink = false))
        
        setHeader("Init", "#8281B1")
    }
}