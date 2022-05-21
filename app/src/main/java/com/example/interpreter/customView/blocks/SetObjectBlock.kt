package com.example.interpreter.customView.blocks

import android.content.Context
import android.util.AttributeSet
import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.ioTypes.InputAny
import com.example.interpreter.ioInterfaces.ioTypes.InputString
import com.example.interpreter.ioInterfaces.ioTypes.OutputAny
import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.instruction.GetVar
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
        
        return setObject(compiler)
    }
    
    private fun setObject(compiler: Compiler): List<Instruction> {
        val inputVariable = getInput((IO.Name.Variable))!!
        val inputKey = getInput((IO.Name.Key))!!
        val inputValue = getInput((IO.Name.Value))!!
        val nameVariable = getLinkInput(inputVariable).name
        
        val assign: MutableList<Instruction> = mutableListOf()
        
        val obj =
            if (nameVariable == IO.Name.Fake) {
                compiler.checkVar(
                    inputVariable.getValue() as String?
                        ?: throw Error("Missing variable to set object")
                ) ?: throw Error("Variable ${inputVariable.getValue()} isn't declare")
                
                val get = GetVar(
                    compiler, inputVariable.getValue() as String
                )
                
                assign.add(get)
                Register(compiler, get)
            } else {
                compiler[IO.Name.Variable]
            }
        
        val key =
            if (getLinkInput(inputKey).name == IO.Name.Fake) com.example.interpreter.vm.instruction.String(
                compiler,
                inputKey.getValue() as String? ?: throw Error("Missing key to set object")
            ) else {
                compiler[IO.Name.Key] as Register
            }
        
        val value =
            if (getLinkInput(inputValue).name == IO.Name.Fake) com.example.interpreter.vm.instruction.String(
                compiler,
                inputValue.getValue() as String? ?: throw Error("Missing value to set object")
            ) else {
                compiler[IO.Name.Value] as Register
            }
        
        assign.add(SetObject(
            compiler,
            obj,
            key,
            value
        ))
        
        return assign
        
    }
    
    init {
        addInput(InputString(IO.Name.Variable, this, "Name or Object"))
        addInput(InputAny(IO.Name.Key, this, "Key"))
        addInput(InputAny(IO.Name.Value, this, "Value"))
        addOutput(OutputAny(IO.Name.out, this, "Object:"))
        
        setHeader("Set Array", "#E9724C")
    }
}