package com.example.interpreter.ioInterfaces

interface Input : IO {
    val isDefault: Boolean
    val isLink: Boolean
    val autocomplete: Boolean
    
    fun parseValue(value: String)
    fun clone(): Input
    fun generateCoupleOutput(): Output
    fun getValue(): Any?
    
    override fun convertToString() =
        "Input, $name, $type, $description, $autocomplete, $isDefault, ${getValue()}"
    
    fun isEqual(input: Input) =
        this.isDefault == input.isDefault &&
                this.autocomplete == input.autocomplete &&
                this.description == input.description &&
                this.parent == input.parent &&
                this.type == input.type
    
    fun isEmpty() = getValue() == null
}