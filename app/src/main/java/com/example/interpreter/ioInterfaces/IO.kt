package com.example.interpreter.ioInterfaces

import com.example.interpreter.customView.blockView.IOContainer

interface IO {
    enum class Type {
        Boolean, Double, String, Function, Any, Null
    }
    
    enum class Name {
        From, To, By,
        Double, String, Boolean, Int, Array,
        Condition, True, False,
        First, Second,
        Variable, Value, Key,
        Fake,
        Print,
    }
    
    val name: Name
    val color: String
    val type: Type
    var parent: IOContainer
    val description: String?
    
    fun convertToString(): String
}