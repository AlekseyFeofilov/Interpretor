package com.example.interpreter.mainBlock.ioTypes

import android.view.View
import com.example.interpreter.mainBlock.IOContainer
import com.example.interpreter.mainBlock.Output

class OutputFunction(
    override val description: String = "",
    override var parent: IOContainer
): Output {
    var default: Unit? = null
    
    override fun getValue() = default
}