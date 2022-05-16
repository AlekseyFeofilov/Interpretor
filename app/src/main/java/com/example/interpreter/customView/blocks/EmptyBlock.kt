package com.example.interpreter.customView.blocks

import android.content.Context
import android.util.AttributeSet
import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.vm.instruction.Instruction

abstract class EmptyBlock @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : BlockView(context, attrs) {

}