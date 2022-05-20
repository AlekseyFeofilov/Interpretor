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
import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.instruction.Bool
import com.example.interpreter.vm.instruction.Instruction
import com.example.interpreter.vm.instruction.Math
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
        return listOf(compare(compiler))
    }
    
    private fun compare(compiler: Compiler): Bool {
        val first = compiler[IO.Name.First] as Register
        val second = compiler[IO.Name.Second] as Register
        return Bool(
            compiler,
            Math(
                compiler,
                listOf(
                    Math.TRegister(first),
                    comparing[spinner.selectedItem.toString()]!!,
                    Math.TRegister(second),
                )
            )
        )
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
        
        spinner = (((binding.listOfInputLinearLayout.getChildAt(2) as LinearLayout)
            .getChildAt(0) as LinearLayout)
            .getChildAt(2) as Spinner)
        
        setHeader("Compare", "#EC8532")
        
        setSpinner()
    }
}