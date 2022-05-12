package com.example.interpreter.ioInterfaces

interface IO {
    companion object{
        enum class Type {
            Boolean, Double, String, Function
        }
    }
    
    val type: Type
    var parent: IOContainer
    val description: String?
    fun getValue(): Any?
    
    fun convertToString(): String
}