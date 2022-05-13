package com.example.interpreter.customView.blocks

import android.R
import android.content.Context
import android.net.wifi.WifiConfiguration.AuthAlgorithm.strings
import android.util.AttributeSet
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import com.example.interpreter.customView.BlockView
import com.example.interpreter.ioInterfaces.Input
import com.example.interpreter.ioInterfaces.ioTypes.InputBoolean
import com.example.interpreter.ioInterfaces.ioTypes.InputDouble
import java.util.Comparator.comparing


class Compare @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BlockView(context, attrs, defStyleAttr) {
    private fun setSpinner() {
        val comparing = arrayListOf("<", ">", "=", "!=", "<=", ">=")
        
        (((binding.listOfInputLinearLayout.getChildAt(2) as LinearLayout)
            .getChildAt(0) as LinearLayout)
            .getChildAt(2) as Spinner)
            .adapter = ArrayAdapter(
            context,
            R.layout.simple_spinner_item,
            comparing.toList()
        )
    }
    
    init {
        addInput(InputDouble("Compare this:", this))
        val input = InputBoolean("By sign:", this, isLink = false)
        addInput(input)
        addInput(InputDouble("With this:", this))
        
        setHeader("Compare", "#EC8532")
        
        setSpinner()
    }
}