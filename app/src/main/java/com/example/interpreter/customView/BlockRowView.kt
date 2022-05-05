package com.example.interpreter.customView

import android.R
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.TableRow
import com.example.interpreter.databinding.BlockRowViewBinding

open class BlockRowView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : TableRow(context, attrs) {
    private val binding = BlockRowViewBinding.inflate(LayoutInflater.from(context), this)
    
    open var inputField = true
    open var definiteInput = false
    
    open var inputTypeField = true
    open var descriptionField = true
    open var expressionField = true
    open var outputField = true
    
    open var expression = "Nop"
    open var description = "Nop"
    open var inputType = BlockView.Companion.InputType.DOUBLE
    
    private fun initComponents() {
        if (!outputField) {
            binding.rowView.removeViewAt(4)
        }
        if (!expressionField) {
            binding.rowView.removeViewAt(3)
        }
        if (!descriptionField) {
            binding.rowView.removeViewAt(2)
        }
        if (!inputTypeField) {
            binding.rowView.removeViewAt(1)
        }
        if (!inputField) {
            binding.rowView.removeViewAt(0)
        }
    }
    
    private fun setDefaultExpression() {
        if (descriptionField) binding.expressionTextView.text = expression
    }
    
    private fun setDescription() {
        if (descriptionField) binding.descriptionTextView.text = description
    }
    
    private fun setDefiniteInputType() {
        if (!definiteInput) return
        
        val type = when (inputType) {
            BlockView.Companion.InputType.BOOLEAN -> "Boolean"
            BlockView.Companion.InputType.DOUBLE -> "Double"
            BlockView.Companion.InputType.STRING -> "String"
            BlockView.Companion.InputType.FUNCTION -> "Function"
        }
        
        val spinnerArray: MutableList<String> = ArrayList()
        spinnerArray.add(type)
        
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            context, R.layout.simple_spinner_item, spinnerArray
        )
        
        binding.typeSpinner.adapter = adapter
    }
    
    open fun overrideData() {
    
    }
    
    init {
        overrideData()
        initComponents()
        setDescription()
        setDefaultExpression()
        setDefiniteInputType()
    }
}