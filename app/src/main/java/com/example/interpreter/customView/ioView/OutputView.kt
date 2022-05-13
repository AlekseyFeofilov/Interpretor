package com.example.interpreter.customView.ioView

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import com.example.interpreter.ioInterfaces.Output
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import com.example.interpreter.R
import com.example.interpreter.databinding.OutputViewBinding

class OutputView(context: Context?) : LinearLayout(context) {
    private val binding = OutputViewBinding.inflate(LayoutInflater.from(context), this)
    private var output: Output? = null
    
    fun setDescription(description: String?) {
        if (output == null) return
        
        if (!description.isNullOrEmpty() && !output!!.description.isNullOrEmpty()) {
            binding.outputDescriptionTextView.text = description
        }
    }
    
    private fun setColor() {
        (binding.outputLinearLayout.getChildAt(1) as RadioButton).buttonTintList =
            ColorStateList.valueOf(Color.parseColor(output!!.color))
    }
    
    private fun setOutputAppearance(drawable: Drawable?) {
        binding.outputRadioButton.buttonDrawable = drawable
    }
    
    @Suppress("SameParameterValue")
    private fun setLinkAppearance(appearance: InputView.Companion.Appearance) {
        when (appearance) {
            InputView.Companion.Appearance.Triangular -> setOutputAppearance(
                ContextCompat.getDrawable(context, android.R.drawable.ic_media_play)
            )
            else -> {
            
            }
        }
    }
    
    fun initComponents(output: Output) {
        this.output = output
        setColor()
        setLinkAppearance(InputView.Companion.Appearance.Triangular)
        
        if (output.description.isNullOrEmpty()) binding.outputLinearLayout.removeViewAt(0)
    }
}