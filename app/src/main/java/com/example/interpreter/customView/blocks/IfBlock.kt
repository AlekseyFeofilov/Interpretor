package com.example.interpreter.customView.blocks

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.ioTypes.InputBoolean
import com.example.interpreter.ioInterfaces.ioTypes.InputFunction
import com.example.interpreter.ioInterfaces.ioTypes.OutputFunction
import com.example.interpreter.vm.instruction.Instruction
import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.Executor
import com.example.interpreter.vm.instruction.If
import com.example.interpreter.vm.instruction.Input
import com.example.interpreter.vm.instruction.Nop

class IfBlock @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BlockView(context, attrs, defStyleAttr) {
    override fun compile(compiler: Compiler): List<Instruction> {
        super.compile(compiler)
        return listOf(getIfInstruction(compiler), Input(compiler, context as AppCompatActivity))
    }
    
    //todo: сделать везде проверки на подключёность запрашивать у компилятора
    private fun getIfInstruction(compiler: Compiler): If {
        return If(
            compiler,
            listOf(
                getInputExecutor(compiler, IO.Name.Condition),
                getInputExecutor(compiler, IO.Name.True),
                getInputExecutor(compiler, IO.Name.False)
            )
        )
    }
    
    init {
        addInput(InputBoolean(IO.Name.Condition, this, "Condition", isDefault = false))
        addInput(InputFunction(IO.Name.True, this, "If condition = true"))
        addInput(InputFunction(IO.Name.False, this, "Else"))
        
        setHeader("If", "#0B6E4F")
    }
}