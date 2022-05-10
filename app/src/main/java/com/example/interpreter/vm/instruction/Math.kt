package com.example.interpreter.vm.instruction

import android.util.Log
import com.example.interpreter.vm.Env
import com.example.interpreter.vm.awaitLR
import java.lang.Error
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1
import kotlin.reflect.full.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

//TODO: more operators
@Serializable
@Suppress("UNUSED", "UNUSED_PARAMETER", "MemberVisibilityCanBePrivate", "PropertyName")
class Math : Instruction {
    private var tokens: MutableList<@Contextual Any>
    
    companion object {
        @JvmStatic fun getStaticFunc(obj: KClass<*>, name: kotlin.String): KFunction<*> {
            @Suppress("UNCHECKED_CAST")
            return (obj.companionObjectInstance!!::class as KClass<Any>).memberFunctions.first { it.name == name }
        }
        
        @JvmStatic fun getStaticField(obj: KClass<*>, name: kotlin.String): KProperty1<Any, *> {
            @Suppress("UNCHECKED_CAST")
            return (obj.companionObjectInstance!!::class as KClass<Any>).memberProperties.first { it.name == name }
        }
        
        @JvmStatic fun getFunc(obj: Any, name: kotlin.String): KFunction<*> {
            @Suppress("UNCHECKED_CAST")
            return obj::class.memberFunctions.first { it.name == name }
        }
        
        @JvmStatic fun toInstruction(obj: Any): Instruction{
            @Suppress("UNCHECKED_CAST")
            return getFunc(obj, "toInstruction").call(obj) as Instruction
        }
        
        @JvmStatic fun toNumber(obj: Any, env: Env): Double{
            @Suppress("UNCHECKED_CAST")
            val v1 = toInstruction(obj)
    
            if(v1 !is Number){
                if(v1 is String){
                    return try {
                        env[v1.value].toNumber()
                    }catch (e: NumberFormatException){
                        Double.NaN
                    }
                }else
                    throw Error("Internal Error v1 is not a Number Type")
            }
            
            return v1.toNumber()
        }
    
        val reflectMap = mutableMapOf<kotlin.String, KClass<*>>()
        
        init {
            for(tokens in Math::class.nestedClasses) {
                if (tokens.isSubclassOf(TokenOP::class) && tokens != TokenOP::class){
                    reflectMap[
                            getStaticField(tokens, "operator").get(tokens) as kotlin.String
                    ] = tokens
                }
            }
        }
    }
    
    abstract class Token{
        val TAG: kotlin.String = javaClass.simpleName
        
        open fun toInstruction(): Instruction{
            throw Error("class {$TAG} don't convert to Instruction")
        }
    }
    
    abstract class TokenOP : Token(){
        companion object{
            const val operator = ""
            const val weight = -1
//            @JvmStatic fun exec(rt: TRuntime, env: Env): Class<*>? = null
        }
        
        open fun exec(rt: TRuntime, env: Env): Any? = null
    }
    
    class TNumber(val value: Number) : Token(){
        override fun toInstruction(): Instruction = value
    }
    
    class TRegister(val value: Register) : Token(){
        fun toToken(env: Env): Token{
            val ret = awaitLR(value.exec(env))
            
            if(ret is String) return TVar(ret)
            if(ret is Number) return TNumber(ret)
            
            throw Error("TRegister not found convert type")
        }
    }
    
    class TVar(val value: String) : Token(){
        override fun toInstruction(): Instruction = value
    }
    
    class TFunc1(val value: kotlin.String): TokenOP(){
        companion object{
            const val operator = ""
            @JvmStatic val functions = mapOf(
                Pair("sqrt", fun(v1: Double): Double = sqrt(v1)),
            )
            const val weight = 100
        }
        
        override fun exec(rt: TRuntime, env: Env){
            val v1 = toNumber(rt.pop(), env)
            
            rt.push(functions[value]!!(v1))
        }
    }
    
    class TFunc2(val value: kotlin.String): TokenOP(){
        companion object{
            const val operator = ""
            @JvmStatic val functions = mapOf(
                Pair("pow", fun(v1: Double, v2: Double): Double = v1.pow(v2)),
            )
            const val weight = 100
        }
        
        override fun exec(rt: TRuntime, env: Env){
            val v2 = toNumber(rt.pop(), env)
            val v1 = toNumber(rt.pop(), env)
            
            rt.push(functions[value]!!(v1, v2))
        }
    }
    
    class TPlus : TokenOP(){
        companion object{
            const val operator = "+"
            const val weight = 4
        }
        
        override fun exec(rt: TRuntime, env: Env){
            val v2 = toNumber(rt.pop(), env)
            val v1 = toNumber(rt.pop(), env)
        
            rt.push(v1 + v2)
        }
    }
    
    class TMinus : TokenOP(){
        companion object{
            const val operator = "-"
            const val weight = 4
        }
    
        override fun exec(rt: TRuntime, env: Env){
            val v2 = toNumber(rt.pop(), env)
            val v1 = toNumber(rt.pop(), env)
        
            rt.push(v1 - v2)
        }
    }
    
    class TMul : TokenOP(){
        companion object{
            const val operator = "*"
            const val weight = 5
        }
    
        override fun exec(rt: TRuntime, env: Env){
            val v2 = toNumber(rt.pop(), env)
            val v1 = toNumber(rt.pop(), env)
        
            rt.push(v1 * v2)
        }
    }
    
    class TDiv : TokenOP(){
        companion object{
            const val operator = "/"
            const val weight = 5
        }
    
        override fun exec(rt: TRuntime, env: Env){
            val v2 = toNumber(rt.pop(), env)
            val v1 = toNumber(rt.pop(), env)
        
            rt.push(v1 / v2)
        }
    }
    
    class TLBrk : TokenOP(){
        companion object{
            const val operator = "("
            const val weight = 0
        }
    
        override fun exec(rt: TRuntime, env: Env){
            TODO("Error")
        }
    }
    
    class TRBrk : TokenOP(){
        companion object{
            const val operator = ")"
            const val weight = 0
        }
    
        override fun exec(rt: TRuntime, env: Env){
            TODO("Error")
        }
    }
    
    class TRuntime(private val tokens: List<Any>){
        private val stack: ArrayDeque<Any> = ArrayDeque()
        
        fun exec(env: Env): Instruction {
            val tIt = tokens.iterator()
            
            while (tIt.hasNext()){
                val nexted = tIt.next()
                
                if(nexted::class.isSubclassOf(TRegister::class)){
                    push(toNumber(getFunc(nexted, "toToken").call(nexted, env) as Token, env))
                    continue
                }
                
                if(!nexted::class.isSubclassOf(TokenOP::class)){
                    push(toNumber(nexted, env))
                    continue
                }
                
                getFunc(nexted, "exec").call(nexted, this, env)
            }
            
            return toInstruction(this.pop())
        }
        
        fun pop(): Any{
            if (stack.any()) return stack.removeLast()
            
            throw Error("Math runtime error stack is end")
        }
        
        fun push(value: Double){
            stack.add(TNumber(Number(value)))
        }
    }
    
    override fun exec(env: Env) = sequence {
//        val ret = TRuntime(tokens).exec(env)
//        Log.i(TAG + "DEMO", ret.toString())
        yield(Object(hashMapOf(
            "out" to TRuntime(tokens).exec(env)
        )))
    }.iterator()
    
    @Throws(Error::class)
//    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(math: List<Token>): super() {
        val calcQueue = mutableListOf<Any>()
        val operatorStack = mutableListOf<Any>()
    
        for(i in math){
            if(i is TNumber || i is TRegister || i is TVar){
                calcQueue.add(i)
                continue
            }
            
            val reflectOP: Any = i
        
            if(reflectOP::class == TRBrk::class){
                var op: Any = Token::class
            
                while(operatorStack.any()){
                    op = operatorStack.removeLast()
                    if(op::class == TLBrk::class) break
                    calcQueue.add(op)
                }
            
                if(op::class != TLBrk::class) throw Error("Unexpected ')'")
            }else{
                val priority = getStaticField(reflectOP::class, "weight").get(reflectOP::class) as Int
            
                while(operatorStack.any() && reflectOP::class != TLBrk::class){
                    val op = operatorStack.removeLast()
                
                    if(priority > getStaticField(op::class, "weight").get(op::class) as Int){
                        operatorStack.add(op)
                        break
                    }
                
                    if(op::class != TLBrk::class) calcQueue.add(op)
                }
            
                operatorStack.add(reflectOP)
            }
        }
    
        operatorStack.reversed().forEach{
            if(it::class == TLBrk::class) throw Error("Unexpected '('")
            calcQueue.add(it)
        }
    
        tokens = calcQueue
    }
    
    @Throws(Error::class)
//    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(math: kotlin.String): super() {
//        val reflectMap = mutableMapOf<kotlin.String, KClass<*>>()
//
//        for(tokens in this::class.nestedClasses) {
//            if (tokens.isSubclassOf(TokenOP::class) && tokens != TokenOP::class){
//                reflectMap[
//                    getStaticField(tokens, "operator").get(tokens) as kotlin.String
//                ] = tokens
//            }
//        }
        
        Log.i(TAG, reflectMap.toString())
        
        var results: MatchResult? = """(?:\s+)?([-+]?\d*\.?\d*(?:[eE][-+]?\d+)?)(?:\s+)?([A-Za-z][A-Za-z\d]*)?(?:\s+)?([^A-Za-z\d\s]*)(?:\s+)?""".toRegex().find(math)
        
        val calcQueue = mutableListOf<Any>()
        val operatorStack = mutableListOf<Any>()
        
        while(results != null) {
            if(results.groups[1]?.value.let { it != null && it != "" }){
                calcQueue.add(TNumber(Number(results.groups[1]!!.value.toDouble())))
            }
            
            @Suppress("NestedLambdaShadowedImplicitParameter")
            for(i in listOf(Pair(results.groups[2]?.value, true), Pair(results.groups[3]?.value, false))){
                if(i.first == null || i.first == "") continue
                
                val reflectOP: Any? = if(i.second){
                    when {
                        TFunc1.functions.containsKey(i.first) -> {
                            TFunc1(i.first!!)
                        }
                        TFunc2.functions.containsKey(i.first) -> {
                            TFunc2(i.first!!)
                        }
                        else -> {
                            calcQueue.add(TVar(String(i.first!!)))
                            continue
                        }
                    }
                }else reflectMap[i.first.toString()]?.createInstance()
                
                if(reflectOP == null) throw Error("Unexpected symbol '${i.first}'")
                
                if(reflectOP::class == TRBrk::class){
                    var op: Any = Token::class
                    
                    while(operatorStack.any()){
                        op = operatorStack.removeLast()
                        if(op::class == TLBrk::class) break
                        calcQueue.add(op)
                    }
                    
                    if(op::class != TLBrk::class) throw Error("Unexpected ')'")
                }else{
                    val priority = getStaticField(reflectOP::class, "weight").get(reflectOP::class) as Int //getStaticFunc(reflectOP::class, "weight").call(reflectOP::class.companionObjectInstance, results) as Int
                    
                    while(operatorStack.any() && reflectOP::class != TLBrk::class){
                        val op = operatorStack.removeLast()
                        
                        if(priority > getStaticField(op::class, "weight").get(op::class) as Int){ //getStaticFunc(op::class, "weight").call(op::class.companionObjectInstance, results) as Int){
                            operatorStack.add(op)
                            break
                        }
                        
                        if(op::class != TLBrk::class) calcQueue.add(op)
                    }
                    
                    operatorStack.add(reflectOP)
                }
            }
    
            results = results.next()
        }
    
        operatorStack.reversed().forEach{
            if(it::class == TLBrk::class) throw Error("Unexpected '('")
            calcQueue.add(it)
        }
        
        tokens = calcQueue
    }
}