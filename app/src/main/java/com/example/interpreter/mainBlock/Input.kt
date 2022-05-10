package com.example.interpreter.mainBlock

interface Input : IO {
    val isDefault: Boolean
    //todo: apply color setting
    val color: String
    val autocomplete: Boolean
    
    fun parse(value: String)
    fun clone(): Input
}