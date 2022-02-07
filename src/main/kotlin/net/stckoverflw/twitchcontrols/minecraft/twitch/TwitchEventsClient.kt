package net.stckoverflw.twitchcontrols.minecraft.twitch

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import com.github.twitch4j.pubsub.PubSubSubscription
import com.github.twitch4j.pubsub.events.ChannelBitsEvent
import com.github.twitch4j.pubsub.events.ChannelSubGiftEvent
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import net.minecraft.client.MinecraftClient
import net.stckoverflw.twitchcontrols.minecraft.EventManager
import net.stckoverflw.twitchcontrols.minecraft.action.TwitchExecutorData
import net.stckoverflw.twitchcontrols.util.JSON
import net.stckoverflw.twitchcontrols.util.playEventSound
import java.util.*

class TwitchEventsClient(playerUUID: UUID, target: String, token: String? = null) {

    val twitch4jClient: TwitchClient = TwitchClientBuilder.builder()
        .withDefaultAuthToken(OAuth2Credential("twitch", token))
        .withEnablePubSub(true)
        .withEnableHelix(true)
        .build()

    private var subscriptions = ArrayList<PubSubSubscription>()

    init {
        val credential = if (token != null) OAuth2Credential("twitch", token) else null
        twitch4jClient.pubSub.connect()

        subscriptions += listOf(
            twitch4jClient.pubSub.listenForChannelPointsRedemptionEvents(credential, target),
            twitch4jClient.pubSub.listenForFollowingEvents(credential, target),
            twitch4jClient.pubSub.listenForSubscriptionEvents(credential, target),
            twitch4jClient.pubSub.listenForChannelSubGiftsEvents(credential, target),
            twitch4jClient.pubSub.listenForCheerEvents(credential, target),
        )

        EventManager.twitchEvents.forEach {
            twitch4jClient.pubSub.eventManager.onEvent(it.eventClass) { event ->
                val player = MinecraftClient.getInstance().server?.playerManager?.getPlayer(playerUUID)
                    ?: error("Couldn't find player with that uuid")
                val activeProfile = EventManager.activeProfile[player.uuid] ?: return@onEvent
                activeProfile.actions.forEach { (eventData, actionData) ->
                    EventManager.actions.forEach { currentAction ->
                        if (currentAction.actionId == JSON.encodeToJsonElement(actionData).jsonObject["action"]
                                .toString().replace("\"", "")
                        ) {
                            val run = it.runEventSafe(event, eventData, player)
                            if (run != null) {
                                if (run.first) player.playEventSound()
                                if (eventData is BitMultipleEventData) {
                                    for (i in 0 until (event as ChannelBitsEvent).data.bitsUsed) {
                                        if (run.first) currentAction.runSafe(
                                            player,
                                            TwitchExecutorData(run.second),
                                            actionData
                                        )
                                    }
                                } else if (eventData is SubGiftMultipleEventData) {
                                    for (i in 0 until (event as ChannelSubGiftEvent).data.count) {
                                        if (run.first) currentAction.runSafe(
                                            player,
                                            TwitchExecutorData(run.second),
                                            actionData
                                        )
                                    }
                                } else if (run.first) currentAction.runSafe(
                                    player,
                                    TwitchExecutorData(run.second),
                                    actionData
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun close() {
        twitch4jClient.pubSub.close()
    }
}