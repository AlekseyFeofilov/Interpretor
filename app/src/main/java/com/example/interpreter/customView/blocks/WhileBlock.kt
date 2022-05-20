package com.example.interpreter.customView.blocks

import android.content.Context
import android.util.AttributeSet
import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.ioTypes.*
import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.Executor
import com.example.interpreter.vm.instruction.If
import com.example.interpreter.vm.instruction.Instruction
import com.example.interpreter.vm.instruction.While

class WhileBlock @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BlockView(context, attrs, defStyleAttr) {
    override fun compile(compiler: Compiler): List<Instruction> {
        super.compile(compiler)
        return listOf(getWhileInstruction(compiler))
    }
    
    private fun getWhileInstruction(compiler: Compiler): While {
        return While(
            compiler,
            listOf(
                getInputExecutor(compiler, IO.Name.Condition),
                getInputExecutor(compiler, IO.Name.True),
            )
        )
    }
    
    init {
        addInput(InputBoolean(IO.Name.Condition, this, "condition: Boolean", isDefault = false))
        addInput(InputFunction(IO.Name.True, this, "action"))
        setHeader("While", "#6B8C9E")
    }
}