package com.example.interpreter.ioInterfaces.ioTypes

import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.customView.blockView.IOContainer
import com.example.interpreter.ioInterfaces.Output

open class OutputBoolean (
    override val name: IO.Name,
    override var parent: IOContainer,
    override val description: String = "",
    val default: Boolean? = null
): Output {
    override val color = "#6CD4FF"
    override val type = IO.Type.Boolean
    
    override fun getValue() = default
}