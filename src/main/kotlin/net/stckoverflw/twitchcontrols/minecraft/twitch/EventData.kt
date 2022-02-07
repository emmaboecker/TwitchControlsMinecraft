package net.stckoverflw.twitchcontrols.minecraft.twitch

import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import net.stckoverflw.twitchcontrols.minecraft.twitch.impl.*
import java.util.*

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("type")
sealed class EventData

@Serializable
@SerialName(channelPointEventId)
data class ChannelPointRedemptionEventData(
    val title: String,
    val id: String = UUID.randomUUID().toString()
) : EventData() {
    override fun toString(): String = "title = \"$title\""
}

@Serializable
@SerialName(followEventId)
data class FollowEventData(
    val id: String = UUID.randomUUID().toString()
) : EventData() {
    override fun toString(): String = "user follow"
}

@Serializable
@SerialName(subscribeEventId)
data class SubscribeEventData(
    @Contextual val requiredMonths: IntRange? = null,
    val id: String = UUID.randomUUID().toString()
) : EventData() {
    override fun toString(): String =
        "subscribe ".plus(if (requiredMonths != null) "with min months ${requiredMonths.first} and max ${requiredMonths.last}" else "")
}

@Serializable
@SerialName(subGiftSingularEventId)
data class SubGiftSingularEventData(
    val useGifterName: Boolean,
    @Contextual val amountRange: IntRange?,
    val id: String = UUID.randomUUID().toString()
) : EventData() {
    override fun toString(): String =
        "action triggered for every sub gift. amount range: $amountRange using sub gifter's name $useGifterName"
}

@Serializable
@SerialName(subGiftMultipleEventId)
data class SubGiftMultipleEventData(
    @Contextual val amountRange: IntRange? = null,
    val id: String = UUID.randomUUID().toString()
) : EventData() {
    override fun toString(): String =
        "one action for multiple subs gifted ".plus(if (amountRange != null) "with min months ${amountRange.first} and max ${amountRange.last}" else "")
}

@Serializable
@SerialName(bitSingularEventId)
data class BitSingularEventData(
    @Contextual val amountRange: IntRange? = null,
    val id: String = UUID.randomUUID().toString()
) : EventData() {
    override fun toString(): String =
        "action repeated for every bit cheered ".plus(if (amountRange != null) "with min bits ${amountRange.first} and max ${amountRange.last}" else "")
}

@Serializable
@SerialName(bitMultipleId)
data class BitMultipleEventData(
    @Contextual val amountRange: IntRange? = null,
    val id: String = UUID.randomUUID().toString()
) : EventData() {
    override fun toString(): String =
        "one action for all bits gifted ".plus(if (amountRange != null) "with min bits ${amountRange.first} and max ${amountRange.last}" else "")
}
