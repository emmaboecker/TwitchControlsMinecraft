package net.stckoverflw.twitchcontrols.minecraft.twitch

import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import net.stckoverflw.twitchcontrols.minecraft.twitch.impl.channelPointEventId
import net.stckoverflw.twitchcontrols.minecraft.twitch.impl.followEventId
import net.stckoverflw.twitchcontrols.minecraft.twitch.impl.subGiftSingularEventId
import net.stckoverflw.twitchcontrols.minecraft.twitch.impl.subscribeEventId

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("type")
sealed class EventData

@Serializable
@SerialName(channelPointEventId)
data class ChannelPointRedemptionEventData(
    val title: String
) : EventData() {
    override fun toString(): String = "title = \"$title\""
}

@Serializable
@SerialName(followEventId)
data class FollowEventData(
    val filler: String
) : EventData() {
    override fun toString(): String = "user follow"
}

@Serializable
@SerialName(subscribeEventId)
data class SubscribeEventData(
    @Contextual val requiredMonths: IntRange? = null
) : EventData() {
    override fun toString(): String =
        "subscibe ".plus(if (requiredMonths != null) "with min months ${requiredMonths.first} and max ${requiredMonths.last}" else "")
}

@Serializable
@SerialName(subGiftSingularEventId)
data class SubGiftSingularEventData(
    val useGifterName: Boolean
) : EventData() {
    override fun toString(): String =
        "action triggered for every sub gift. using sub gifter's name $useGifterName"
}

@Serializable
@SerialName(subGiftSingularEventId)
data class SubGiftMultipleEventData(
    @Contextual val amountRange: IntRange? = null
) : EventData() {
    override fun toString(): String =
        "one action for multiple subs gifted".plus(if (amountRange != null) "with min months ${amountRange.first} and max ${amountRange.last}" else "")
}