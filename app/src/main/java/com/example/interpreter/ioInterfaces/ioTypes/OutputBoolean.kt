package com.example.interpreter.ioInterfaces.ioTypes

import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.IOContainer
import com.example.interpreter.ioInterfaces.Output

class OutputBoolean (
    override val description: String = "",
    override var parent: IOContainer
): Output {
    override val color = "#6CD4FF"
    override val type = IO.Companion.Type.Boolean
    var default: Boolean? = null
    
    override fun getValue() = default
}