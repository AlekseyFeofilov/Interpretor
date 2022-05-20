package com.example.interpreter.vm.instruction

import android.util.Log
import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.Env
import com.example.interpreter.vm.awaitLR
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.modules.subclass
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlin.Error
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1
import kotlin.reflect.full.*
import kotlin.math.*

@Serializable
@Suppress("UNUSED", "UNUSED_PARAMETER", "MemberVisibilityCanBePrivate", "PropertyName",
    "RemoveRedundantQualifierName", "SERIALIZER_TYPE_INCOMPATIBLE"
)
class Math : Instruction {
    private var tokens: MutableList<@Polymorphic Any>
    
//    class Serializer : KSerializer<Math> {
//        override val descriptor: SerialDescriptor = buildClassSerialDescriptor(Math::class.qualifiedName ?: "*EMPTY qualifiedName*") {
//            element<Int>("id")
//            element<Boolean>("_isBasic")
//            element<MutableList<Any>>("tokens")
//        }
//
//        override fun serialize(encoder: Encoder, value: Math) {
//            encoder.encodeStructure(descriptor){
//                encodeIntElement(descriptor, 0, value.id)
//                encodeBooleanElement(descriptor, 1, value.isBasic)
////                enco(descriptor, 2, value.v)
//            }
//        }
//
//        override fun deserialize(decoder: Decoder): Math {
//            return decoder.decodeStructure(descriptor){
//                var value = 0.0
//
//                while (true) {
//                    when (val index = decodeElementIndex(descriptor)) {
//                        0 -> value = decodeDoubleElement(descriptor, 2)
//                        CompositeDecoder.DECODE_DONE -> break
//                        else -> error("Unexpected index: $index")
//                    }
//                }
//
//                Math(Compiler.FCompiler(), "")
//            }
//        }
//    }
    
//    class Serializer : KSerializer<Token> {
//        override val descriptor: SerialDescriptor = buildClassSerialDescriptor(this::class.qualifiedName ?: "*EMPTY qualifiedName*") {
//            element<kotlin.String>("TAG")
//        }
//
//        override fun serialize(encoder: Encoder, value: Token) {
//            encoder.encodeStructure(descriptor){
//                encodeStringElement(descriptor, 0, value.TAG)
//            }
//        }
//
//        override fun deserialize(decoder: Decoder): Token {
//            throw Error("not deserialize")
//        }
//    }
    
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
                        env[v1.toString()].toNumber()
                    }catch (e: NumberFormatException){
                        Double.NaN
                    }
                }else
                    throw Error("Internal Error v1 is not a Number Type")
            }
            
            return v1.toNumber()
        }
    
        val reflectMap = mutableMapOf<kotlin.String, KClass<*>>()
        val module = SerializersModule {
            polymorphic(Any::class) {
                subclass(TNumber::class)
                subclass(TVar::class)
                subclass(TRegister::class)
                subclass(TFunc1::class)
                subclass(TFunc2::class)
                subclass(TLogicNot::class)
                subclass(TBitNot::class)
                subclass(TPow::class)
                subclass(TMul::class)
                subclass(TDiv::class)
                subclass(TMod::class)
                subclass(TPlus::class)
                subclass(TMinus::class)
                subclass(TShl::class)
                subclass(TShr::class)
                subclass(TUShr::class)
                subclass(TLogicLess::class)
                subclass(TLogicLessEQ::class)
                subclass(TLogicGreater::class)
                subclass(TLogicGreaterEQ::class)
                subclass(TLogicEqual::class)
                subclass(TLogicEqualNot::class)
                subclass(TBitAnd::class)
                subclass(TBitXor::class)
                subclass(TBitOr::class)
                subclass(TLogicAnd::class)
                subclass(TLogicOr::class)
                subclass(TLBrk::class)
                subclass(TRBrk::class)
            }
        }
        
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
    
    
    @Serializable
    @SerialName("Token")
    abstract class Token{
        val TAG: kotlin.String = javaClass.simpleName
        
        open fun toInstruction(): Instruction{
            throw Error("class {$TAG} don't convert to Instruction")
        }
    }
    
    @Serializable
    @SerialName("TokenOP")
    abstract class TokenOP : Token(){
        companion object{
            const val operator = ""
            const val weight = -1
        }
        
        open fun exec(rt: TRuntime, env: Env): Any? = null
    }
    
    @Serializable
    @SerialName("TNumber")
    class TNumber(val value: Number) : Token(){
        override fun toInstruction(): Instruction = value
    }
    
    @Serializable
    @SerialName("TRegister")
    class TRegister(val value: Register) : Token(){
        fun toToken(env: Env): Token{
            val ret = awaitLR(value.exec(env))
            
            if(ret is String) return TVar(ret)
            if(ret is Number) return TNumber(ret)
            if(ret is Int) return TNumber(Number(Compiler.FCompiler(), ret.toNumber()))
            
            throw Error("TRegister not found convert type")
        }
    }
    
    @Serializable
    @SerialName("TVar")
    class TVar(val value: String) : Token(){
        override fun toInstruction(): Instruction = value
    }
    
    @Serializable
    @SerialName("TFunc1")
    class TFunc1(val value: kotlin.String): TokenOP(){
        companion object{
            const val operator = ""
            @JvmStatic val functions = mapOf(
                "sqrt" to fun(v1: Double): Double = sqrt(v1),
                "abs" to fun(v1: Double): Double = abs(v1),
                "cos" to fun(v1: Double): Double = kotlin.math.cos(v1),
                "sin" to fun(v1: Double): Double = kotlin.math.sin(v1),
                "acos" to fun(v1: Double): Double = kotlin.math.acos(v1),
                "asin" to fun(v1: Double): Double = kotlin.math.asin(v1),
                "tan" to fun(v1: Double): Double = kotlin.math.tan(v1),
                "atan" to fun(v1: Double): Double = kotlin.math.atan(v1),
                "ceil" to fun(v1: Double): Double = kotlin.math.ceil(v1),
                "floor" to fun(v1: Double): Double = kotlin.math.floor(v1),
                "round" to fun(v1: Double): Double = kotlin.math.round(v1),
                "exp" to fun(v1: Double): Double = kotlin.math.exp(v1),
                "ln" to fun(v1: Double): Double = kotlin.math.ln(v1),
                "log10" to fun(v1: Double): Double = kotlin.math.log10(v1),
                "log2" to fun(v1: Double): Double = kotlin.math.log2(v1),
            )
            const val weight = 101
        }
        
        override fun exec(rt: TRuntime, env: Env){
            val v1 = toNumber(rt.pop(), env)
            
            rt.push(functions[value]!!(v1))
        }
    }
    
    @Serializable
    @SerialName("TFunc2")
    class TFunc2(val value: kotlin.String): TokenOP(){
        companion object{
            const val operator = ""
            @JvmStatic val functions = mapOf(
                "pow" to fun(v1: Double, v2: Double): Double = v1.pow(v2),
                "log" to fun(v1: Double, v2: Double): Double = kotlin.math.log(v1, v2),
                "max" to fun(v1: Double, v2: Double): Double = kotlin.math.max(v1, v2),
                "min" to fun(v1: Double, v2: Double): Double = kotlin.math.min(v1, v2),
            )
            const val weight = 100
        }
        
        override fun exec(rt: TRuntime, env: Env){
            val v2 = toNumber(rt.pop(), env)
            val v1 = toNumber(rt.pop(), env)
            
            rt.push(functions[value]!!(v1, v2))
        }
    }
    
    @Serializable
    @SerialName("TLogicNot")
    class TLogicNot : TokenOP(){
        companion object{
            const val operator = "!"
            const val weight = 20
        }
    
        override fun exec(rt: TRuntime, env: Env){
            val v1 = toNumber(rt.pop(), env)
        
            rt.push(if(v1 == 0.0) 1.0 else 0.0)
        }
    }
    
    @Serializable
    @SerialName("TBitNot")
    class TBitNot : TokenOP(){
        companion object{
            const val operator = "~"
            const val weight = 20
        }
    
        override fun exec(rt: TRuntime, env: Env){
            val v1 = toNumber(rt.pop(), env)
        
            rt.push(v1.toLong().inv().toDouble())
        }
    }
    
    @Serializable
    @SerialName("TPow")
    class TPow : TokenOP(){
        companion object{
            const val operator = "**"
            const val weight = 19
        }
    
        override fun exec(rt: TRuntime, env: Env){
            val v2 = toNumber(rt.pop(), env)
            val v1 = toNumber(rt.pop(), env)
        
            rt.push(v1.pow(v2))
        }
    }
    
    @Serializable
    @SerialName("TMul")
    class TMul : TokenOP(){
        companion object{
            const val operator = "*"
            const val weight = 18
        }
        
        override fun exec(rt: TRuntime, env: Env){
            val v2 = toNumber(rt.pop(), env)
            val v1 = toNumber(rt.pop(), env)
            
            rt.push(v1 * v2)
        }
    }
    
    @Serializable
    @SerialName("TDiv")
    class TDiv : TokenOP(){
        companion object{
            const val operator = "/"
            const val weight = 18
        }
        
        override fun exec(rt: TRuntime, env: Env){
            val v2 = toNumber(rt.pop(), env)
            val v1 = toNumber(rt.pop(), env)
            
            rt.push(v1 / v2)
        }
    }
    
    @Serializable
    @SerialName("TMod")
    class TMod : TokenOP(){
        companion object{
            const val operator = "%"
            const val weight = 18
        }
    
        override fun exec(rt: TRuntime, env: Env){
            val v2 = toNumber(rt.pop(), env)
            val v1 = toNumber(rt.pop(), env)
        
            rt.push(v1.mod(v2))
        }
    }
    
    @Serializable
    @SerialName("TPlus")
    class TPlus : TokenOP(){
        companion object{
            const val operator = "+"
            const val weight = 17
        }
        
        override fun exec(rt: TRuntime, env: Env){
            val v2 = toNumber(rt.pop(), env)
            val v1 = toNumber(rt.pop(), env)
            
            rt.push(v1 + v2)
        }
    }
    
    @Serializable
    @SerialName("TMinus")
    class TMinus : TokenOP(){
        companion object{
            const val operator = "-"
            const val weight = 17
        }
        
        override fun exec(rt: TRuntime, env: Env){
            val v2 = toNumber(rt.pop(), env)
            val v1 = toNumber(rt.pop(), env)
            
            rt.push(v1 - v2)
        }
    }
    
    @Serializable
    @SerialName("TShl")
    class TShl : TokenOP(){
        companion object{
            const val operator = "<<"
            const val weight = 16
        }
        
        override fun exec(rt: TRuntime, env: Env){
            val v2 = toNumber(rt.pop(), env)
            val v1 = toNumber(rt.pop(), env)
            
            rt.push(v1.toLong().shl(v2.toInt()).toDouble())
        }
    }
    
    @Serializable
    @SerialName("TShr")
    class TShr : TokenOP(){
        companion object{
            const val operator = ">>"
            const val weight = 16
        }
        
        override fun exec(rt: TRuntime, env: Env){
            val v2 = toNumber(rt.pop(), env)
            val v1 = toNumber(rt.pop(), env)
            
            rt.push(v1.toLong().shr(v2.toInt()).toDouble())
        }
    }
    
    @Serializable
    @SerialName("TUShr")
    class TUShr : TokenOP(){
        companion object{
            const val operator = ">>>"
            const val weight = 16
        }
        
        override fun exec(rt: TRuntime, env: Env){
            val v2 = toNumber(rt.pop(), env)
            val v1 = toNumber(rt.pop(), env)
            
            rt.push(v1.toLong().ushr(v2.toInt()).toDouble())
        }
    }
    
    @Serializable
    @SerialName("TLogicLess")
    class TLogicLess : TokenOP(){
        companion object{
            const val operator = "<"
            const val weight = 15
        }
        
        override fun exec(rt: TRuntime, env: Env){
            val v2 = toNumber(rt.pop(), env)
            val v1 = toNumber(rt.pop(), env)
            
            rt.push(if(v1.toLong() < v2.toLong()) 1.0 else 0.0)
        }
    }
    
    @Serializable
    @SerialName("TLogicLessEQ")
    class TLogicLessEQ : TokenOP(){
        companion object{
            const val operator = "<="
            const val weight = 15
        }
        
        override fun exec(rt: TRuntime, env: Env){
            val v2 = toNumber(rt.pop(), env)
            val v1 = toNumber(rt.pop(), env)
            
            rt.push(if(v1.toLong() <= v2.toLong()) 1.0 else 0.0)
        }
    }
    
    @Serializable
    @SerialName("TLogicGreater")
    class TLogicGreater : TokenOP(){
        companion object{
            const val operator = ">"
            const val weight = 15
        }
        
        override fun exec(rt: TRuntime, env: Env){
            val v2 = toNumber(rt.pop(), env)
            val v1 = toNumber(rt.pop(), env)
            
            rt.push(if(v1.toLong() > v2.toLong()) 1.0 else 0.0)
        }
    }
    
    @Serializable
    @SerialName("TLogicGreaterEQ")
    class TLogicGreaterEQ : TokenOP(){
        companion object{
            const val operator = ">="
            const val weight = 15
        }
        
        override fun exec(rt: TRuntime, env: Env){
            val v2 = toNumber(rt.pop(), env)
            val v1 = toNumber(rt.pop(), env)
            
            rt.push(if(v1.toLong() >= v2.toLong()) 1.0 else 0.0)
        }
    }
    
    @Serializable
    @SerialName("TLogicEqual")
    class TLogicEqual : TokenOP(){
        companion object{
            const val operator = "=="
            const val weight = 15
        }
        
        override fun exec(rt: TRuntime, env: Env){
            val v2 = toNumber(rt.pop(), env)
            val v1 = toNumber(rt.pop(), env)
            
            rt.push(if(v1.toLong() == v2.toLong()) 1.0 else 0.0)
        }
    }
    
    @Serializable
    @SerialName("TLogicEqualNot")
    class TLogicEqualNot : TokenOP(){
        companion object{
            const val operator = "!="
            const val weight = 15
        }
        
        override fun exec(rt: TRuntime, env: Env){
            val v2 = toNumber(rt.pop(), env)
            val v1 = toNumber(rt.pop(), env)
            
            rt.push(if(v1.toLong() != v2.toLong()) 1.0 else 0.0)
        }
    }
    
    @Serializable
    @SerialName("TBitAnd")
    class TBitAnd : TokenOP(){
        companion object{
            const val operator = "&"
            const val weight = 14
        }
        
        override fun exec(rt: TRuntime, env: Env){
            val v2 = toNumber(rt.pop(), env)
            val v1 = toNumber(rt.pop(), env)
            
            rt.push(v1.toLong().and(v2.toLong()).toDouble())
        }
    }
    
    @Serializable
    @SerialName("TBitXor")
    class TBitXor : TokenOP(){
        companion object{
            const val operator = "^"
            const val weight = 14
        }
        
        override fun exec(rt: TRuntime, env: Env){
            val v2 = toNumber(rt.pop(), env)
            val v1 = toNumber(rt.pop(), env)
            
            rt.push(v1.toLong().xor(v2.toLong()).toDouble())
        }
    }
    
    @Serializable
    @SerialName("TBitOr")
    class TBitOr : TokenOP(){
        companion object{
            const val operator = "|"
            const val weight = 14
        }
        
        override fun exec(rt: TRuntime, env: Env){
            val v2 = toNumber(rt.pop(), env)
            val v1 = toNumber(rt.pop(), env)
            
            rt.push(v1.toLong().or(v2.toLong()).toDouble())
        }
    }
    
    @Serializable
    @SerialName("TLogicAnd")
    class TLogicAnd : TokenOP(){
        companion object{
            const val operator = "&&"
            const val weight = 13
        }
        
        override fun exec(rt: TRuntime, env: Env){
            val v2 = toNumber(rt.pop(), env)
            val v1 = toNumber(rt.pop(), env)
            
            rt.push(if((v1 != 0.0) && (v2 != 0.0)) 1.0 else 0.0)
        }
    }
    
    @Serializable
    @SerialName("TLogicOr")
    class TLogicOr : TokenOP(){
        companion object{
            const val operator = "||"
            const val weight = 13
        }
        
        override fun exec(rt: TRuntime, env: Env){
            val v2 = toNumber(rt.pop(), env)
            val v1 = toNumber(rt.pop(), env)
    
            rt.push(if((v1 != 0.0) || (v2 != 0.0)) 1.0 else 0.0)
        }
    }
    
    @Serializable
    @SerialName("TLBrk")
    class TLBrk : TokenOP(){
        companion object{
            const val operator = "("
            const val weight = 0
        }
    
        override fun exec(rt: TRuntime, env: Env){
            throw Error("Runtime math error, found TLBrk token")
        }
    }
    
    @Serializable
    @SerialName("TRBrk")
    class TRBrk : TokenOP(){
        companion object{
            const val operator = ")"
            const val weight = 0
        }
    
        override fun exec(rt: TRuntime, env: Env){
            throw Error("Runtime math error, found TRBrk token")
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
            stack.add(TNumber(Number(Compiler.FCompiler(), value)))
        }
    }
    
    override fun exec(env: Env) = sequence {
//        val ret = TRuntime(tokens).exec(env)
//        Log.i(TAG + "DEMO", ret.toString())
        yield(this@Math)
        yield(Object(Compiler.FCompiler(),
            "out" to TRuntime(tokens).exec(env)
        ))
    }.iterator()
    
    @Throws(Error::class)
    constructor(compiler: Compiler, math: List<Token>): super(compiler) {
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
                val priority = getStaticField(reflectOP::class, "weight").get(reflectOP::class) as kotlin.Int
            
                while(operatorStack.any() && reflectOP::class != TLBrk::class){
                    val op = operatorStack.removeLast()
                
                    if(priority > getStaticField(op::class, "weight").get(op::class) as kotlin.Int){
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
    constructor(compiler: Compiler, math: kotlin.String): super(compiler) {
        var results: MatchResult? = """(?:\s+)?([-+]?\d*\.?\d*(?:[eE][-+]?\d+)?)(?:\s+)?([A-Za-z][A-Za-z\d]*)?(?:\s+)?([^A-Za-z\d\s]*)(?:\s+)?""".toRegex().find(math)
        
        val calcQueue = mutableListOf<Any>()
        val operatorStack = mutableListOf<Any>()
        
        while(results != null) {
            if(results.groups[1]?.value.let { it != null && it != "" }){
                calcQueue.add(TNumber(Number(compiler, results.groups[1]!!.value.toDouble())))
            }
            
            @Suppress("NestedLambdaShadowedImplicitParameter")
            for(i in listOf(Pair(results.groups[2]?.value, true), Pair(results.groups[3]?.value, false))){
                if(i.first == null || i.first == "") continue
                
                val reflectOP: Any? = if(i.second){
                    when {
                        i.first == "PI" -> {
                            calcQueue.add(TNumber(Number(compiler, PI)))
                            continue
                        }
                        i.first == "E" -> {
                            calcQueue.add(TNumber(Number(compiler, E)))
                            continue
                        }
                        TFunc1.functions.containsKey(i.first) -> {
                            TFunc1(i.first!!)
                        }
                        TFunc2.functions.containsKey(i.first) -> {
                            TFunc2(i.first!!)
                        }
                        else -> {
                            calcQueue.add(TVar(String(compiler, i.first!!)))
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
                    val priority = getStaticField(reflectOP::class, "weight").get(reflectOP::class) as kotlin.Int
                    
                    while(operatorStack.any() && reflectOP::class != TLBrk::class){
                        val op = operatorStack.removeLast()
                        
                        if(priority > getStaticField(op::class, "weight").get(op::class) as kotlin.Int){
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