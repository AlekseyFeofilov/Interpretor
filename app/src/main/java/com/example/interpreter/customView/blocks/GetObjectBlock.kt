package com.example.interpreter.customView.blocks

import android.content.Context
import android.util.AttributeSet
import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.ioTypes.*
import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.instruction.GetObject
import com.example.interpreter.vm.instruction.Instruction
import com.example.interpreter.vm.instruction.Register

class GetObjectBlock @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BlockView(context, attrs, defStyleAttr) {
    override fun compile(compiler: Compiler): List<Instruction> {
        super.compile(compiler)
        return listOf(
            GetObject(
                compiler,
                compiler[IO.Name.Variable],
                compiler[IO.Name.Key] as Register
            )
        )
    }
    
    init {
        addInput(InputObject(IO.Name.Variable, this, "Name or Object"))
        addInput(InputAny(IO.Name.Key, this, "Key"))
        addOutput(OutputAny(IO.Name.out, this, "Value:"))
        addOutput(OutputAny(IO.Name.out1, this, "Object:"))
        
        setHeader("Get Array", "#8B8BAE")
    }
}