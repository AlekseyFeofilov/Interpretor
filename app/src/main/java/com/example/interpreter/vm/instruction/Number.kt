package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Env
import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.awaitLR
import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import java.lang.Error

@Suppress("RemoveRedundantQualifierName", "FunctionName")
@Serializable(with = Number.Serializer::class)
open class Number : Instruction {
    @SerialName("_isBasic")
    override val isBasic: Boolean = true
    
    @Transient
    private var value: @Contextual Any = 0.0
    
    @OptIn(ExperimentalSerializationApi::class)
    class Serializer : KSerializer<Number>{
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor(Number::class.qualifiedName ?: "*EMPTY qualifiedName*") {
            element<kotlin.Int>("id")
            element<kotlin.Boolean>("_isBasic")
            element<kotlin.Double>("value")
        }
    
        override fun serialize(encoder: Encoder, value: Number) {
            encoder.encodeStructure(descriptor){
                encodeIntElement(descriptor, 0, value.id)
                encodeBooleanElement(descriptor, 1, value.isBasic)
                encodeNullableSerializableElement(descriptor, 2, Double.serializer(), try{ value.v }catch (e: Error){ null })
            }
        }
    
        override fun deserialize(decoder: Decoder): Number {
            return decoder.decodeStructure(descriptor){
                var value = 0.0
                
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> value = decodeDoubleElement(descriptor, 2)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }
                
                Number(Compiler.FCompiler(), value)
            }
        }
    }
    
    val v: kotlin.Double
        get() = _toNumber(value)
    
    override fun exec(env: Env) = sequence<Instruction> { yield(this@Number) }.iterator()
    override fun toNumber(): Double = _toNumber(value)
    override fun toString(): kotlin.String = _toNumber(value).toString()
    
    private fun _toNumber(value: Any): kotlin.Double{
        if(value is kotlin.Double) return value
        if(value is Number) return value.v
        if(value is Int) return value.toNumber()
        if(value is Bool) return if(value.toBool()) 1.0 else 0.0
        if(value is String) return value.toString().toDoubleOrNull() ?: Double.NaN
        if(value is Register) return _toNumber(awaitLR(value.exec()))
        
        throw Error("Runtime Error 'to number' instruction not entry")
    }
    
    
    constructor(compiler: Compiler, value: kotlin.Double = 0.0) : super(compiler) {
        this.value = value
    }
    
    constructor(compiler: Compiler, value: Instruction) : super(compiler){
        this.value = value
    }
}