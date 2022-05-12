package com.example.interpreter.ioInterfaces.ioTypes

import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.IOContainer
import com.example.interpreter.ioInterfaces.Output

class OutputDouble(
    override val description: String = "",
    override var parent: IOContainer
): Output {
    override val type = IO.Companion.Type.Double
    var default: Double? = null
    
    override fun getValue() = default
}