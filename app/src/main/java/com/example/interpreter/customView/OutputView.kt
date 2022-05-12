package com.example.interpreter.customView

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.interpreter.Point
import com.example.interpreter.databinding.OutputViewBinding

class OutputView(context: Context?) : LinearLayout(context) {
    private val binding = OutputViewBinding.inflate(LayoutInflater.from(context), this)
    
    private var outputField = false
    private var descriptionField = false
    
    fun setDescription(description: String?) {
        if (!description.isNullOrEmpty() && descriptionField) {
            binding.outputDescriptionTextView.text = description
        }
    }
    
    fun initComponents(
        outputField: Boolean,
        descriptionInputField: Boolean,
    ) {
        this.outputField = outputField
        this.descriptionField = descriptionInputField
        
        initRow()
    }
    
    private fun initRow() {
        if (!outputField) this.removeViewAt(1)
        if (!descriptionField) this.removeViewAt(0)
    }
    
    fun getCoordinatesInParent() = Point(this.x, this.y)
}