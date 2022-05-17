package com.example.interpreter.ioInterfaces

interface Output : IO {
    val input: Input?
    override fun convertToString() = "Output, $name, $type, $description"
}