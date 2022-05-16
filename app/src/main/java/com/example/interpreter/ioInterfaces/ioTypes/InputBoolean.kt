package com.example.interpreter.ioInterfaces.ioTypes

import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.customView.blockView.IOContainer
import com.example.interpreter.ioInterfaces.Input

class InputBoolean(
    override val name: IO.Name,
    override var parent: IOContainer,
    override val description: String = "",
    override val autocomplete: Boolean = false,
    override val isDefault: Boolean = true,
    override val isLink: Boolean = true
) : Input {
    override val type = IO.Type.Boolean
    override val color = "#6CD4FF"
    var default: Boolean? = null
    
    override fun parseValue(value: String) {}
    
    override fun clone(): Input {
        return InputBoolean(name, parent, description, autocomplete, isDefault, isLink)
    }
    
    override fun getValue() = default
}