package com.example.interpreter.customView.blocks

import android.content.Context
import android.util.AttributeSet
import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.ioTypes.InputBoolean
import com.example.interpreter.ioInterfaces.ioTypes.InputFunction
import com.example.interpreter.ioInterfaces.ioTypes.OutputFunction
import com.example.interpreter.vm.instruction.Instruction
import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.Executor
import com.example.interpreter.vm.instruction.If

class IfBlock @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BlockView(context, attrs, defStyleAttr) {
    override fun compile(compiler: Compiler): List<Instruction> {
        super.compile(compiler)
        return listOf(getIfInstruction(compiler))
    }
    
    private fun getInputExecutor(name: IO.Name, compiler: Compiler): Executor {
        compiler.push()
        compiler[name]
        return compiler.pop()
    }
    
    private fun getIfInstruction(compiler: Compiler): If {
        return If(
            compiler,
            listOf(
                getInputExecutor(IO.Name.Condition, compiler),
                getInputExecutor(IO.Name.True, compiler),
                getInputExecutor(IO.Name.False, compiler)
            )
        )
    }
    
    init {
        addInput(InputBoolean(IO.Name.Condition, this, "Condition", true, false))
        addOutput(OutputFunction(IO.Name.True, this, "If condition = true"))
        addOutput(OutputFunction(IO.Name.False, this, "Else"))
        
        setHeader("If", "#0B6E4F")
    }
}