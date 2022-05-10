package com.example.interpreter.mainBlock

import android.view.View

interface IO {
    var parent: IOContainer
    val description: String?
    fun getValue(): Any?
}