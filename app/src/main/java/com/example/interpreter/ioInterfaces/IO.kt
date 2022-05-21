package com.example.interpreter.ioInterfaces

import com.example.interpreter.customView.blockView.IOContainer

interface IO {
    enum class Type {
        Boolean, Double, Int, String, Object, Function, Any, Null
    }
    
    @Suppress("EnumEntryName")
    enum class Name {
        From, To, By,
        Double, String, Boolean, Int, Array,
        Condition, True, False,
        First, Second,
        Variable, Value, Key,
        Fake,
        Print,
        out, out1, out2, out3,
        Iterate, Body
    }
    
    val name: Name
    val color: String
    val type: Type
    var parent: IOContainer
    val description: String?
    
    fun convertToString(): String
}