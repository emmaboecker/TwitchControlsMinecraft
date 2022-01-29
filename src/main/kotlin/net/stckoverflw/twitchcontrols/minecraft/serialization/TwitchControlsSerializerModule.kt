package net.stckoverflw.twitchcontrols.minecraft.serialization

import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

val TwitchControlsSerializerModule = SerializersModule {
    contextual(IdentifierSerializer)
    contextual(IntRangeSerializer)
}
