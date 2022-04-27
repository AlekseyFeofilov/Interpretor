package com.example.interpreter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.interpreter.vm.VM

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        val test: VM = VM()
    }
}