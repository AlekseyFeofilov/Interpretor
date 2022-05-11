package com.example.interpreter.mainBlock.ioTypes

import android.graphics.Color
import android.view.View
import com.example.interpreter.mainBlock.IOContainer
import com.example.interpreter.mainBlock.Input

class InputFunction(
    override val description: String = "",
    override var parent: IOContainer,
    override val autocomplete: Boolean = false
) : Input {
    override val isDefault = false
    override val color = "#8B80F9"
    var default: Unit? = null
    
    override fun parse(value: String) {
        //do nothing because isDefault = false
    }
    
    override fun clone(): Input {
        return InputFunction(this.description, this.parent, this.autocomplete)
    }
    
    override fun getValue() = default
}