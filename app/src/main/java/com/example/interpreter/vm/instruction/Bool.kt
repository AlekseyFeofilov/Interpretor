package com.example.interpreter.vm.instruction

import com.example.interpreter.vm.Env
import com.example.interpreter.vm.awaitLR
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import java.lang.Error

@Suppress("RemoveRedundantQualifierName", "FunctionName")
@Serializable(with = Bool.Serializer::class)
open class Bool : Instruction {
    @SerialName("_isBasic")
    override val isBasic: Boolean = true
    
    @Transient
    private var value: @Contextual Any = false
    
    class Serializer : KSerializer<Bool> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor(Bool::class.qualifiedName ?: "*EMPTY qualifiedName*") {
            element<kotlin.Int>("id")
            element<kotlin.Boolean>("_isBasic")
            element<kotlin.Boolean>("value")
        }
        
        override fun serialize(encoder: Encoder, value: Bool) {
            encoder.encodeStructure(descriptor){
                encodeIntElement(descriptor, 0, value.id)
                encodeBooleanElement(descriptor, 1, value.isBasic)
                encodeBooleanElement(descriptor, 2, value.v)
            }
        }
        
        override fun deserialize(decoder: Decoder): Bool {
            return decoder.decodeStructure(descriptor){
                var value = false
                
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> value = decodeBooleanElement(descriptor, 2)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }
    
                Bool(value)
            }
        }
    }
    
    val v: kotlin.Boolean
        get() = _toBool(value)
    
    override fun exec(env: Env) = sequence<Instruction> { yield(this@Bool) }.iterator()
    override fun toNumber(): Double = if(_toBool(value)) 1.0 else 0.0
    override fun toString(): kotlin.String = _toBool(value).toString()
    fun toBool(): kotlin.Boolean = _toBool(value)
    
    private fun _toBool(value: Any): kotlin.Boolean{
        if(value is Boolean) return value
        if(value is Number) return value.toNumber() != 0.0
        if(value is String) return value.toString().isNotEmpty()
        if(value is Object) return value.value.values.any()
        if(value is Register) return _toBool(awaitLR(value.exec()))
        
        throw Error("Runtime Error 'to bool' instruction not entry")
    }
    
    constructor(value: kotlin.Boolean) : super() {
        this.value = value
    }
    
    constructor(value: Instruction) : super(){
        this.value = value
    }
}