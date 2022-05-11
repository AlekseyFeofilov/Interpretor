package com.example.interpreter.mainBlock.ioTypes

import android.graphics.Color
import android.view.View
import com.example.interpreter.mainBlock.IOContainer
import com.example.interpreter.mainBlock.Input

class InputString(
    override val description: String = "",
    override var parent: IOContainer,
    override val autocomplete: Boolean = false
) : Input {
    override val isDefault = true
    override val color = "#AB954D"
    var default: String? = null
    
    override fun parse(value: String){
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