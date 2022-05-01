package com.example.interpreter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.interpreter.databinding.ActivityMainBinding
import com.example.interpreter.vm.VM

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
//        val test: VM = VM()
    }
}