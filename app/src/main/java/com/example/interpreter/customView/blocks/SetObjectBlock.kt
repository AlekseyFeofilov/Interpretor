package com.example.interpreter.customView.blocks

import android.content.Context
import android.util.AttributeSet
import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.ioTypes.InputAny
import com.example.interpreter.ioInterfaces.ioTypes.InputString
import com.example.interpreter.ioInterfaces.ioTypes.OutputAny
import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.instruction.Instruction
import com.example.interpreter.vm.instruction.Register
import com.example.interpreter.vm.instruction.SetObject

class SetObjectBlock @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BlockView(context, attrs, defStyleAttr) {
    override fun compile(compiler: Compiler): List<Instruction> {
        super.compile(compiler)
        
        return listOf(
            SetObject(
                compiler,
                compiler[IO.Name.Variable],
                compiler[IO.Name.Key] as Register,
                compiler[IO.Name.Value] as Register
            )
        )
    }
    
    init {
        addInput(InputString(IO.Name.Variable, this, "Name or Object"))
        addInput(InputAny(IO.Name.Key, this, "Key"))
        addInput(InputAny(IO.Name.Value, this, "Value"))
        addOutput(OutputAny(IO.Name.out, this, "Object:"))
    
        setHeader("Set Array", "#E9724C")
    }
}