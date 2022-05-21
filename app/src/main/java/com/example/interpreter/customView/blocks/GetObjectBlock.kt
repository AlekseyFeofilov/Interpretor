package com.example.interpreter.customView.blocks

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.ioTypes.*
import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.instruction.GetObject
import com.example.interpreter.vm.instruction.GetVar
import com.example.interpreter.vm.instruction.Instruction
import com.example.interpreter.vm.instruction.Register
import com.example.interpreter.vm.instruction.String

class GetObjectBlock @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BlockView(context, attrs, defStyleAttr) {
    override fun compile(compiler: Compiler): List<Instruction> {
        super.compile(compiler)
        return listOf(getObject(compiler))
    }
    
    private fun getObject(compiler: Compiler): GetObject {
        val inputVariable = getInput((IO.Name.Variable))!!
        val inputKey = getInput((IO.Name.Key))!!
        val nameVariable = getLinkInput(inputVariable).name
        
        val obj =
            if (nameVariable == IO.Name.Fake) {
                compiler.checkVar(
                    inputVariable.getValue() as kotlin.String?
                        ?: {
                            Log.i("TAG", "Missing variable to get object")
                            throw Error("Missing variable to get object")
                        }.toString()
                ) ?: throw Error("Variable ${inputVariable.getValue()} isn't declare")
            } else {
                compiler[IO.Name.Variable]
            }
    
        GetVar(
                    compiler, inputVariable.getValue() as kotlin.String
                )
        
        val key = if (getLinkInput(inputKey).name == IO.Name.Fake) String(
            compiler,
            inputKey.getValue() as kotlin.String? ?: throw Error("Missing key to get object")
        ) else {
            compiler[IO.Name.Value] as Register
        }
        
        return GetObject(
            compiler,
            obj,
            key
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