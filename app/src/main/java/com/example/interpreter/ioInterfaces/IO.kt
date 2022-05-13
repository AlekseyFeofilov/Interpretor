package com.example.interpreter.ioInterfaces

import com.example.interpreter.customView.blockView.IOContainer

interface IO {
    companion object{
        enum class Type {
            Boolean, Double, String, Function, Null
        }
    }
    
    val color: String
    val type: Type
    var parent: IOContainer
    val description: String?
    fun getValue(): Any?
    
    fun convertToString(): String
}