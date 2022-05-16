package com.example.interpreter.ioInterfaces.ioTypes

import android.content.Context
import android.util.AttributeSet
import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.customView.blockView.IOContainer
import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.vm.instruction.Bool
import com.example.interpreter.vm.instruction.Instruction

class FakeOutputBoolean(name: IO.Name, parent: IOContainer, description: String = "") :
    OutputBoolean(name, parent, description) {
    
    class FakeBlock @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null
    ) : BlockView(context, attrs){
        /*override fun compile(): List<Instruction> {
            return listOf(Bool())
        }*/
    }
}