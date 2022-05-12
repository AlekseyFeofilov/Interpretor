package com.example.interpreter.customView.blocks

import android.content.Context
import android.util.AttributeSet
import com.example.interpreter.customView.BlockView
import com.example.interpreter.ioInterfaces.ioTypes.InputBoolean
import com.example.interpreter.ioInterfaces.ioTypes.InputDouble
import com.example.interpreter.ioInterfaces.ioTypes.InputString
import com.example.interpreter.ioInterfaces.ioTypes.OutputBoolean

class BlockWhile @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BlockView(context, attrs, defStyleAttr) {
      init{
          addInput(InputBoolean("condition:", this))
          addInput(InputDouble("condition:", this, true))
          addInput(InputString("condition:", this, true))
          addOutput(OutputBoolean("action:", this))
          setHeader("While", "#0D5697")
      }
}