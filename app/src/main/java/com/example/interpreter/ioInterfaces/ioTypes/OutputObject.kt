package com.example.interpreter.ioInterfaces.ioTypes

import com.example.interpreter.customView.blockView.IOContainer
import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.Output

class OutputObject (
    override val name: IO.Name,
    override var parent: IOContainer,
    override val description: String = "",
) : Output {
    override val color = "#732C2C"
    override val type = IO.Type.Object
}