package com.example.interpreter.customView.blockWhile

import android.content.Context
import android.util.AttributeSet
import android.widget.TableRow
import com.example.interpreter.customView.BlockRowView
import com.example.interpreter.customView.BlockView

class RowCondition @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : BlockRowView(context, attrs) {
    override fun overrideData() {
        expressionField = false
        outputField = false
        description = "condition"
        definiteInput = true
        inputType = BlockView.Companion.InputType.BOOLEAN
    }
/*
    override var expressionField = false
    override var outputField = false
    override var description = "condition"
    override var definiteInput = true
    override var inputType = BlockView.Companion.InputType.BOOLEAN
*/
}