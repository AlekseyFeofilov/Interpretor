package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Env
import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.awaitLR
import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import java.lang.Error
import java.lang.Exception

@Suppress("FunctionName", "RemoveRedundantQualifierName")
@Serializable(with = String.Serializer::class)
class String : Instruction {
    @SerialName("_isBasic")
    override val isBasic: Boolean = true
    
    @Transient
    private var value: @Contextual Any = ""
    
    @OptIn(ExperimentalSerializationApi::class)
    class Serializer : KSerializer<String> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor(String::class.qualifiedName ?: "*EMPTY qualifiedName*") {
            element<kotlin.Int>("id")
            element<kotlin.Boolean>("_isBasic")
            element<kotlin.String>("value")
        }
        
        override fun serialize(encoder: Encoder, value: String) {
            encoder.encodeStructure(descriptor){
                encodeIntElement(descriptor, 0, value.id)
                encodeBooleanElement(descriptor, 1, value.isBasic)
                encodeNullableSerializableElement(descriptor, 2, kotlin.String.serializer(), try{ value.v }catch (e: Error){ null }catch (e: Exception){ null })
            }
        }
        
        override fun deserialize(decoder: Decoder): String {
            return decoder.decodeStructure(descriptor){
                var value = ""
                
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> value = decodeStringElement(descriptor, 2)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }
                
                String(Compiler.FCompiler(), value)
            }
        }
    }
    
    val v: kotlin.String
        get() = _toString(value)
    
    override fun exec(env: Env) = sequence<Instruction> { yield(this@String) }.iterator()
    override fun toNumber(): Double = _toString(value).toDouble()
    override fun toString(): kotlin.String = _toString(value)
    
    private fun _toString(value: Any): kotlin.String{
        if(value is kotlin.String) return value
        if(value is String) return value.v
        if(value is Int) return value.toString()
        if(value is Bool) return if(value.toBool()) "True" else "False"
        if(value is Number) return value.toNumber().toString()
        if(value is Object) return value.toString()
        if(value is Register) return _toString(awaitLR(value.exec()))
        
        throw Error("Runtime Error 'to string' instruction not entry")
    }
    
    constructor(compiler: Compiler, value: kotlin.String) : super(compiler) {
        this.value = value
    }
    
    constructor(compiler: Compiler, value: Instruction) : super(compiler){
        this.value = value
    }
}