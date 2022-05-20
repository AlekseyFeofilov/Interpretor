package com.example.interpreter

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.window.layout.WindowMetrics
import androidx.window.layout.WindowMetricsCalculator
import com.example.interpreter.databinding.ActivityMainBinding
import com.example.interpreter.vm.VM

// settings:
var isButtonForConsoleVisibility = false


lateinit var metrics: WindowMetrics

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        metrics = WindowMetricsCalculator.getOrCreate()
            .computeCurrentWindowMetrics(this)

//        val test: VM = VM()
    }
    
    fun getContext(): Context {
        return this
    }
}