package com.example.interpreter.ioInterfaces.ioTypes

import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.IOContainer
import com.example.interpreter.ioInterfaces.Input
import java.lang.Double.parseDouble
import java.lang.Error

class InputDouble(
    override val description: String = "",
    override var parent: IOContainer,
    override val autocomplete: Boolean = false,
    override val isDefault: Boolean = true,
    override val isLink: Boolean = true
) : Input {
    override val type = IO.Companion.Type.Double
    override val color = "#80505B"
    var default: Double? = null
    
    override fun parseValue(value: String) {
        if (value.isEmpty()) {
            default = null
            return
        }
        
        default = try {
            parseDouble(value)
        }
        catch (e: java.lang.NumberFormatException){
            0.0
        }
    }
    
    override fun clone(): Input {
        return InputDouble(description, parent, autocomplete, isDefault, isLink)
    }
    
    override fun getValue() = default
}