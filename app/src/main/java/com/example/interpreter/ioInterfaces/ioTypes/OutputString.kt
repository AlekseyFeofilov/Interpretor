package com.example.interpreter.ioInterfaces.ioTypes

import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.IOContainer
import com.example.interpreter.ioInterfaces.Output

class OutputString(
    override val description: String = "",
    override var parent: IOContainer
) : Output {
    override val type = IO.Companion.Type.String
    var default: String? = null
    
    override fun getValue() = default
}