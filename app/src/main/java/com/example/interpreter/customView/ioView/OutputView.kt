package com.example.interpreter.customView.ioView

import android.content.Context
import com.example.interpreter.ioInterfaces.Output
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.interpreter.databinding.OutputViewBinding

class OutputView(context: Context?) : LinearLayout(context) {
    private val binding = OutputViewBinding.inflate(LayoutInflater.from(context), this)
    private var output: Output? = null
    
    fun setDescription(description: String?) {
        if(output == null) return
        
        if (!description.isNullOrEmpty() && !output!!.description.isNullOrEmpty()) {
            binding.outputDescriptionTextView.text = description
        }
    }
    
    fun initComponents(output: Output) {
        this.output = output
        
        if (output.description.isNullOrEmpty()) binding.outputLinearLayout.removeViewAt(0)
    }
}