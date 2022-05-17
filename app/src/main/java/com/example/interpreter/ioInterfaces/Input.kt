package com.example.interpreter.ioInterfaces

interface Input : IO {
    val isDefault: Boolean
    val isLink: Boolean
    val autocomplete: Boolean
    
    //todo: add field of link to InputView EditText
    //todo: don't parse value, it's compiler job
    fun parseValue(value: String)
    fun clone(): Input
    fun generateCoupleOutput(): Output
    
    override fun convertToString() =
        "Input, $name, $type, $description, $autocomplete, $isDefault, ${getValue()}"
    
    fun isEqual(input: Input) =
        this.isDefault == input.isDefault &&
                this.autocomplete == input.autocomplete &&
                this.description == input.description &&
                this.parent == input.parent &&
                this.type == input.type
    
    fun isEmpty() = getValue() == null
    
    //todo: not parse value from editText, but keep link to this editText so compiler will have access to this view to get string and parse it itself
}