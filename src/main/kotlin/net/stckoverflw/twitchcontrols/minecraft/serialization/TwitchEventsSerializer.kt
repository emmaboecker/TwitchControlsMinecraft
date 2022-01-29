package net.stckoverflw.twitchcontrols.minecraft.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object IntRangeSerializer : KSerializer<IntRange> {

    private const val separator = ':'

    override val descriptor = PrimitiveSerialDescriptor("IntRange", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): IntRange {
        val ints = decoder.decodeString().split(separator).map { it.toInt() }
        return IntRange(ints.first(), ints.last())
    }

    override fun serialize(encoder: Encoder, value: IntRange) {
        encoder.encodeString(value.first.toString() + separator + value.last.toString())
    }

}