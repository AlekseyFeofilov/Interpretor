package com.example.interpreter.customView.blockWhile

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.interpreter.customView.BlockView
import com.example.interpreter.vm.Executor

class BlockWhile @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BlockView(context, attrs, defStyleAttr) {
    override fun overrideData() {
        name = "While"
        headerColor = "#0D5697"
    
        rows = listOf(RowCondition(context), RowAction(context))
    }
    
}