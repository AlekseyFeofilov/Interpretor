package com.example.interpreter.customView.blocks

import android.content.Context
import android.util.AttributeSet
import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.ioTypes.InputString
import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.instruction.*
import com.example.interpreter.vm.instruction.Number
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

class AssignBlock @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BlockView(context, attrs, defStyleAttr) {
    override fun compile(compiler: Compiler): List<Instruction> {
        super.compile(compiler)
        val assignment = getAssignment()
        
        checkAssignment(assignment)
        checkVariableDeclare(compiler, assignment)
        
        val instruction = getInstruction(compiler, assignment)
        return listOf(assign(compiler, assignment.first!!, instruction))
    }
    
    private fun toBool(string: String): Boolean{
        return when{
            string.matches("""\s*true\s*""".toRegex()) -> true
            string.matches("""\s*false\s*""".toRegex()) -> false
            else -> {
                val value = """\s*(\S*)""".toRegex().find(string)
                throw Error("${value!!.groups[1]!!.value} isn't boolean value")
            }
        }
    }
    
    private fun getInstruction(compiler: Compiler, assignment: Pair<String?, String?>): Instruction{
        val input = inputs.find { it.first.name == IO.Name.Value }!!.first
        
        return if(getLinkInput(input).name == IO.Name.Fake) {
            val value = assignment.first!!
            getInstructionByClass(compiler, compiler.checkVar(value)!!, value)
        }
        else getInstructionByCompiler(compiler)
    }
    
    private fun getInstructionByCompiler(compiler: Compiler): Instruction{
        return compiler[IO.Name.Value] as Register
    }
    
    private fun getInstructionByClass(compiler: Compiler, clazz: KClass<Instruction>, value: String): Instruction{
        return when(clazz){
            Number::class -> Math(compiler, value)
            Bool::class -> Bool(compiler, toBool(value))
            else -> {
                val compilerType = Compiler::class.createType()
                val instructionType = Instruction::class.createType()
                val constructor = clazz.constructors.find { it.typeParameters == listOf(compilerType, instructionType) }
            
                return constructor!!.call(compiler, com.example.interpreter.vm.instruction.String(compiler, value))
            }
        }
    }
    
    private fun getAssignment(): Pair<String?, String?> {
        return Pair(
            (inputs.find { it.first.name == IO.Name.Variable }!!.first as InputString).getValue(),
            (inputs.find { it.first.name == IO.Name.Value }!!.first as InputString).getValue()
        )
    }
    
    private fun checkAssignment(assignment: Pair<String?, String?>) {
        if (assignment.first.isNullOrEmpty()) {
            throw Error("Missing variable to assign")
        }
        
        if (assignment.second.isNullOrEmpty()) {
            throw Error("Missing value to assign")
        }
    }
    
    private fun checkVariableDeclare(compiler: Compiler, assignment: Pair<String?, String?>) {
        if (compiler.checkVar(assignment.first!!) == null){
            throw Error("Variable isn't declare")
        }
    }
    
    private fun assign(compiler: Compiler, variable: String, value: Instruction): SetVar {
        return SetVar(compiler, variable, value)
    }
    
    init {
        addInput(InputString(IO.Name.Variable, this, "Variable:", isLink = false))
        addInput(InputString(IO.Name.Value, this, "Value:", isLink = false))
        
        setHeader("Assign", "#DB5764")
    }
}