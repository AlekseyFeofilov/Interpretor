package com.example.interpreter.ioInterfaces.ioTypes

import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.IOContainer
import com.example.interpreter.ioInterfaces.Output

class OutputFunction(
    override val description: String = "",
    override var parent: IOContainer
): Output {
    override val color = "#8B80F9"
    override val type = IO.Companion.Type.Function
    var default: Unit? = null
    
    override fun getValue() = default
}