package com.example.interpreter.ioInterfaces

interface Output : IO {
    override fun convertToString() = "Output, $name, $type, $description"
}