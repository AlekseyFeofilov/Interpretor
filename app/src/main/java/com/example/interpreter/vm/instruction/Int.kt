package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.Env
import com.example.interpreter.vm.awaitLR
import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import java.lang.Error
import java.lang.Exception

@Suppress("RemoveRedundantQualifierName", "FunctionName")
@Serializable(with = Int.Serializer::class)
open class Int : Instruction {
    @SerialName("_isBasic")
    override val isBasic: Boolean = true
    
    @Transient
    private var value: @Contextual Any = 0
    
    @OptIn(ExperimentalSerializationApi::class)
    class Serializer : KSerializer<Int> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor(Int::class.qualifiedName ?: "*EMPTY qualifiedName*") {
            element<kotlin.Int>("id")
            element<kotlin.Boolean>("_isBasic")
            element<kotlin.Double>("value")
        }
        
        override fun serialize(encoder: Encoder, value: Int) {
            encoder.encodeStructure(descriptor){
                encodeIntElement(descriptor, 0, value.id)
                encodeBooleanElement(descriptor, 1, value.isBasic)
                encodeNullableSerializableElement(descriptor, 2, kotlin.Int.serializer(), try{ value.v }catch (e: Exception){ null })
            }
        }
        
        override fun deserialize(decoder: Decoder): Int {
            return decoder.decodeStructure(descriptor){
                var value = 0
                
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> value = decodeIntElement(descriptor, 2)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }
    
                Int(Compiler.FCompiler(), value)
            }
        }
    }
    
    val v: kotlin.Int
        get() = _toInt(value)
    
    override fun exec(env: Env) = sequence<Instruction> { yield(this@Int) }.iterator()
    override fun toNumber(): Double = _toInt(value).toDouble()
    override fun toString(): kotlin.String = _toInt(value).toString()
    
    private fun _toInt(value: Any): kotlin.Int{
        if(value is kotlin.Int) return value
        if(value is Int) return value.v
        if(value is Number) return value.toNumber().toInt()
        if(value is Bool) return if(value.toBool()) 1 else 0
        if(value is String) return value.toString().toIntOrNull() ?: 0
        if(value is Register) return _toInt(awaitLR(value.exec()))
        
        throw Error("Runtime Error 'to Int' instruction not entry")
    }
    
    constructor(compiler: Compiler, value: kotlin.Int = 0) : super(compiler) { this.value = value }
    constructor(compiler: Compiler, value: Instruction) : super(compiler) { this.value = value }
}