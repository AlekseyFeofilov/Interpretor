package com.example.interpreter.customView.blockView

interface Compiler {
    fun compile()
    fun checkError()
    fun typeMismatch()
}