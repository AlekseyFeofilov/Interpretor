package com.example.interpreter.ioInterfaces.ioTypes

import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.customView.blockView.IOContainer
import com.example.interpreter.ioInterfaces.Input
import com.example.interpreter.ioInterfaces.Output

class OutputFunction(
    override val name: IO.Name,
    override var parent: IOContainer,
    override val description: String = "",
    override val input: Input? = null
): Output {
    override val color = "#8B80F9"
    override val type = IO.Type.Function
    var default: Unit? = null
    
    override fun getValue() = default
}