package com.example.interpreter.vm.instruction

import kotlin.String as String
import com.example.interpreter.vm.Env
import com.example.interpreter.vm.Instruction
import java.lang.Error

class Math : Instruction {
    lateinit var tokens: List<Token>
    lateinit var tokensOP: List<TokenOP>
    
    abstract class Token(){
        val TAG: String = javaClass.simpleName
        
        open fun toInstruction(): Instruction{
            throw Error("class {$TAG} don't convert to Instruction")
        }
    }
    
    abstract class TokenOP : Token(){
        companion object{ const val operator = "" }
        abstract fun exec(rt: TRuntime, env: Env)
    }
    
    class TNumber(private val value: Number) : Token(){
        override fun toInstruction(): Instruction = value
    }
    
    class TPlus() : TokenOP(){
        companion object{ const val operator = "+" }
        override fun exec(rt: TRuntime, env: Env){
        
        }
    }
    
    class TRuntime(private val tokens: List<Token>, private val tokens_op: List<TokenOP>){
        private val stack: ArrayDeque<Token> = ArrayDeque()
        private val tIt = tokens.iterator()
        
        fun exec(env: Env): Instruction {
            val it = tokens_op.iterator()
            
            while (it.hasNext()) it.next().exec(this, env)
            
            return this.pop().toInstruction()
        }
        
        fun pop(): Token{
            if (stack.any()) return stack.removeFirst()
            if (tIt.hasNext()) return tIt.next()
            
            throw Error("Math runtime error stack is end")
        }
    }
    
    override fun exec(env: Env) = sequence<Instruction> {
        yield(TRuntime(tokens, tokensOP).exec(env))
    }.iterator()
    
    constructor(math: String): super() {
        val stripedMath = Regex("""\s+""").replace(math, "")
        val reflectMap = mutableMapOf<String, Any>()
        
        for(tokens in this.javaClass.classes) {
            if (TokenOP::class.java.isAssignableFrom(tokens) && tokens != TokenOP::class.java){
                reflectMap[tokens.getField("operator").get(String)!!.toString()] = tokens
            }
        }
    
    
        tokens = listOf()
        tokensOP = listOf()
//        Log.i(TAG, """(-?[\d]*\.?[\d]*)((?:[A-Z][A-Za-z\d]*)*)([\D]?)""".toRegex().find("1AD3D/")!!.groupValues.joinToString())
    }
}