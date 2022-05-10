package com.example.interpreter.customView.blocks

import android.content.Context
import android.util.AttributeSet
import com.example.interpreter.customView.BlockView
import com.example.interpreter.mainBlock.ioTypes.InputBoolean
import com.example.interpreter.mainBlock.ioTypes.InputDouble
import com.example.interpreter.mainBlock.ioTypes.OutputBoolean

class BlockWhile @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BlockView(context, attrs, defStyleAttr) {
      init{
          addInput(InputBoolean("condition:", this))
          addOutput(OutputBoolean("action:", this))
          setHeader("While", "#0D5697")
    
/*
          addOutput(listOf(OutputBoolean("action:", this), OutputBoolean("action2:", this)))
          addInput(listOf(InputBoolean("condition:", this), InputDouble("for test", this, true)))
*/
      }
}