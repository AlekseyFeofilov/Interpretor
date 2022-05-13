package com.example.interpreter.customView.blocks

import android.content.Context
import android.util.AttributeSet
import com.example.interpreter.customView.BlockView
import com.example.interpreter.ioInterfaces.ioTypes.InputBoolean
import com.example.interpreter.ioInterfaces.ioTypes.InputFunction
import com.example.interpreter.ioInterfaces.ioTypes.InputString

class If @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BlockView(context, attrs, defStyleAttr) {
    init {
        addInput(InputBoolean("Condition", this, true, false))
        addInput(InputFunction("If condition = true", this))
        addInput(InputFunction("Else", this))
        
        setHeader("If", "#0B6E4F")
    }
}