package com.example.interpreter.customView.blockWhile

import android.content.Context
import android.util.AttributeSet
import com.example.interpreter.customView.BlockRowView
import com.example.interpreter.customView.BlockView

class RowAction @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : BlockRowView(context, attrs) {
    override fun overrideData() {
        expressionField = false
        description = "action"
        definiteInput = true
        inputType = BlockView.Companion.InputType.FUNCTION
    }
/*
    override var expressionField = false
    override var description = "action"
    override var definiteInput = true
    override var inputType = BlockView.Companion.InputType.FUNCTION
*/
}