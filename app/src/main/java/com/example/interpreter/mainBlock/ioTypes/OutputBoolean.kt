package com.example.interpreter.mainBlock.ioTypes

import android.view.View
import com.example.interpreter.mainBlock.IOContainer
import com.example.interpreter.mainBlock.Output

class OutputBoolean (
    override val description: String = "",
    override var parent: IOContainer
): Output {
    var default: Boolean? = null
    
    override fun getValue() = default
}