package com.example.interpreter.ioInterfaces.ioTypes

import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.IOContainer
import com.example.interpreter.ioInterfaces.Input
import java.lang.Double.parseDouble

class InputDouble(
    override val description: String = "",
    override var parent: IOContainer,
    override val autocomplete: Boolean = false
) : Input {
    override val type = IO.Companion.Type.Double
    override val isDefault = true
    override val color = "#80505B"
    var default: Double? = null
    
    override fun parseValue(value: String) {
        if(value.isEmpty()) {
            default = null
            return
        }
        
        try {
            default = parseDouble(value)
        }
        catch (e: Error) {
            throw Error("It's not Integer")
        }
    }
    
    override fun clone(): Input {
        return InputDouble(this.description, this.parent, this.autocomplete)
    }
    
    override fun getValue() = default
}