package com.example.interpreter.mainBlock.ioTypes

import android.view.View
import com.example.interpreter.mainBlock.IOContainer
import com.example.interpreter.mainBlock.Input

class InputBoolean(
    override val description: String = "",
    override var parent: IOContainer,
) : Input{
    override val autocomplete = false
    override val isDefault = true
    override val color = "#6CD4FF"
    var default: Boolean? = null
    
    override fun parse(value: String){
        if(value.isEmpty()) {
            default = null
            return
        }
        
        default = when(value){
            "true" -> true
            "false" -> false
            else -> {
                throw Error("It's not Boolean")
            }
        }
    }
    
    override fun clone(): Input {
        return InputBoolean(this.description, this.parent)
    }
    
    override fun getValue() = default
}