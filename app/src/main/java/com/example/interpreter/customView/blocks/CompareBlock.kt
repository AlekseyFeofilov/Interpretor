package com.example.interpreter.customView.blocks

import android.content.Context
import android.util.AttributeSet
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.ioTypes.InputBoolean
import com.example.interpreter.ioInterfaces.ioTypes.InputDouble
import com.example.interpreter.ioInterfaces.ioTypes.OutputBoolean
import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.instruction.Bool
import com.example.interpreter.vm.instruction.Instruction
import com.example.interpreter.vm.instruction.Math
import com.example.interpreter.vm.instruction.Object
import com.example.interpreter.vm.instruction.Register

class CompareBlock @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BlockView(context, attrs, defStyleAttr) {
    private val spinner: Spinner
    private val comparing = hashMapOf<String, Math.Token>(
        "<" to Math.TLogicLess(),
        ">" to Math.TLogicGreater(),
        "=" to Math.TLogicEqual(),
        "!=" to Math.TLogicEqualNot(),
        "<=" to Math.TLogicLessEQ(),
        ">=" to Math.TLogicGreaterEQ(),
    )
    
    override fun compile(compiler: Compiler): List<Instruction> {
        super.compile(compiler)
        
        val bool = compare(compiler)
        return listOf(Object(compiler, "out" to bool))
    }
    
    private fun compare(compiler: Compiler): Bool {
        val first = compiler[IO.Name.First] as Register
        val second = compiler[IO.Name.Second] as Register
        val math = Math(
            compiler,
            listOf(
                Math.TRegister(first),
                comparing[spinner.selectedItem.toString()]!!,
                Math.TRegister(second),
            )
        )
        val register = Register(compiler, math, env = compiler.env(), exec = true)
        return Bool(compiler, register)
    }
    
    private fun setSpinner() {
        spinner.adapter = ArrayAdapter(
            context,
            android.R.layout.simple_spinner_item,
            comparing.map { it.key }
        )
    }
    
    init {
        addInput(InputDouble(IO.Name.First, this, "Compare this:"))
        addInput(InputBoolean(IO.Name.By, this, "By sign:", isLink = false))
        addInput(InputDouble(IO.Name.Second, this, "With this:"))
        addOutput(OutputBoolean(IO.Name.out, this, "answer:"))
        
        spinner = (((binding.listOfInputLinearLayout.getChildAt(2) as LinearLayout)
            .getChildAt(0) as LinearLayout)
            .getChildAt(2) as Spinner)
        
        setHeader("Compare", "#EC8532")
        
        setSpinner()
    }
}