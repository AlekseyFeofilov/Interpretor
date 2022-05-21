package com.example.interpreter.customView.blocks

import android.content.Context
import android.util.AttributeSet
import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.ioTypes.InputAny
import com.example.interpreter.ioInterfaces.ioTypes.InputString
import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.instruction.*
import com.example.interpreter.vm.instruction.Number
import kotlin.reflect.KClass
import kotlin.reflect.KTypeParameter
import kotlin.reflect.full.createType

class AssignBlock @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BlockView(context, attrs, defStyleAttr) {
    override fun compile(compiler: Compiler): List<Instruction> {
        super.compile(compiler)
        val assignment = getAssignment()
        checkVariableDeclare(compiler, assignment.first)
        
        val instruction = getInstruction(compiler, assignment)
        //todo: make check type
        return listOf(assign(compiler, assignment.first, instruction))
    }
    
    private fun stringWithoutSpaces(string: String): String? {
        return """\s*(\S+)\s*""".toRegex().find(string)?.groups?.get(1)?.value
    }
    
    private fun toBool(string: String): Boolean {
        return when {
            string.matches("""\s*true\s*""".toRegex()) -> true
            string.matches("""\s*false\s*""".toRegex()) -> false
            else -> {
                val value = """\s*(\S*)""".toRegex().find(string)
                throw Error("${value!!.groups[1]!!.value} isn't boolean value")
            }
        }
    }
    
    private fun getInstruction(
        compiler: Compiler,
        assignment: Pair<String, String?>
    ): Instruction {
        return if (assignment.second != null) {
            getInstructionByClass(compiler, compiler.checkVar(assignment.first)!!, assignment.second!!)
        } else getInstructionByCompiler(compiler)
    }
    
    private fun getInstructionByCompiler(compiler: Compiler): Instruction {
        return compiler[IO.Name.Value] as Register
    }
    
    private fun getInstructionByClass(
        compiler: Compiler,
        clazz: KClass<out Instruction>,
        value: String
    ): Instruction {
        return when (clazz) {
            //приведение типов не может делать компилятор, потому что тут используется математика и toBool
            Number::class, com.example.interpreter.vm.instruction.Int::class -> Register(compiler, Math(compiler, value), env = compiler.env(), exec = true)
            Bool::class -> Bool(compiler, toBool(value))
            else -> {
                val compilerType = Compiler::class.typeParameters[0]
                val instructionType = Instruction::class.typeParameters[0]
                val constructor = clazz.constructors.find {
                    it.typeParameters == listOf(
                        compilerType,
                        instructionType
                    )
                }
                
                return constructor!!.call(
                    compiler,
                    com.example.interpreter.vm.instruction.String(compiler, value)
                )
            }
        }
    }
    
    private fun getAssignment(): Pair<String, String?> {
        val input = getInput(IO.Name.Value) as InputAny
    
        var variable = (getInput(IO.Name.Variable) as InputString).getValue()
            ?: throw Error("Missing variable to assign")
        variable = stringWithoutSpaces(variable)!!

        val value = if (isInputAvailable(input)) null
        else input.getValue() ?: throw Error("Missing value to assign")
        
        return Pair(variable, value)
    }
    
    private fun checkVariableDeclare(compiler: Compiler, variable: String) {
        if (compiler.checkVar(variable) == null) {
            throw Error("Variable isn't declare [${variable}]")
        }
    }
    
    private fun assign(compiler: Compiler, variable: String, value: Instruction): SetVar {
        return SetVar(compiler, variable, value)
    }
    
    init {
        addInput(InputString(IO.Name.Variable, this, "Variable:", isLink = false))
        addInput(InputAny(IO.Name.Value, this, "Value:"))
        
        setHeader("Assign", "#DB5764")
    }
}