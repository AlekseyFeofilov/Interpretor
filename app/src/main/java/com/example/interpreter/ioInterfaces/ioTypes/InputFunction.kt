package com.example.interpreter.ioInterfaces.ioTypes

import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.customView.blockView.IOContainer
import com.example.interpreter.ioInterfaces.Input

class InputFunction(
    override val name: IO.Name,
    override var parent: IOContainer,
    override val description: String = "",
    override val autocomplete: Boolean = false,
    override val isLink: Boolean = true
) : Input {
    override val type = IO.Type.Function
    override val isDefault = false
    override val color = "#8B80F9"
    var default: Unit? = null
    
    override fun parseValue(value: String) { }
    
    override fun clone(): Input {
        return InputFunction(name, parent, description, autocomplete, isLink)
    }
    
    override fun getValue() = default
}