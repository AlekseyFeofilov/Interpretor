package com.example.interpreter.ioInterfaces.ioTypes

import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.IOContainer
import com.example.interpreter.ioInterfaces.Input

class InputBoolean(
    override val description: String = "",
    override var parent: IOContainer,
    override val autocomplete: Boolean = false
) : Input{
    override val type = IO.Companion.Type.Boolean
    override val isDefault = true
    override val color = "#6CD4FF"
    var default: Boolean? = null
    
    override fun parseValue(value: String){ }
    
    override fun clone(): Input {
        return InputBoolean(this.description, this.parent)
    }
    
    override fun getValue() = default
}