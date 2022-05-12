package com.example.interpreter.ioInterfaces.ioTypes

import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.IOContainer
import com.example.interpreter.ioInterfaces.Input

class InputString(
    override val description: String = "",
    override var parent: IOContainer,
    override val autocomplete: Boolean = false
) : Input {
    override val type = IO.Companion.Type.String
    override val isDefault = true
    override val color = "#AB954D"
    var default: String? = null
    
    override fun parseValue(value: String){
        if(value.isEmpty()) {
            default = null
            return
        }
        
        default = value
    }
    
    override fun clone(): Input {
        return InputString(this.description, this.parent, this.autocomplete)
    }
    
    override fun getValue() = default
}