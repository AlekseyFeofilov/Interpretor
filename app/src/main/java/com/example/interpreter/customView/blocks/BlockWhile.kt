package com.example.interpreter.customView.blocks

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.interpreter.customView.BlockView
import com.example.interpreter.customView.ioView.OutputView
import com.example.interpreter.ioInterfaces.ioTypes.*

class BlockWhile @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BlockView(context, attrs, defStyleAttr) {
    init {
        addInput(InputBoolean("condition: Boolean", this, isDefault = false))
        addInput(InputFunction("action", this))
        setHeader("While", "#6B8C9E")
    }
}