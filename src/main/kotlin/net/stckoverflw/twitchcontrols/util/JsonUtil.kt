package net.stckoverflw.twitchcontrols.util

import kotlinx.serialization.json.Json
import net.stckoverflw.twitchcontrols.minecraft.serialization.TwitchControlsSerializerModule

val JSON = Json {
    allowStructuredMapKeys = true

    serializersModule = TwitchControlsSerializerModule
}