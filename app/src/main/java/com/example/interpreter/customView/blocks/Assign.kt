package com.example.interpreter.customView.blocks

import android.content.Context
import android.util.AttributeSet
import com.example.interpreter.customView.BlockView
import com.example.interpreter.ioInterfaces.ioTypes.InputString

class Assign @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BlockView(context, attrs, defStyleAttr) {
    init {
        addInput(InputString("Variable:", this, isLink = false))
        addInput(InputString("Value:", this, isLink = false))
        
        setHeader("Assign", "#FF4797")
    }
}