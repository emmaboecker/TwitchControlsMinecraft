package net.stckoverflw.twitchcontrols.minecraft.twitch

import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import com.github.twitch4j.pubsub.PubSubSubscription
import net.minecraft.entity.player.PlayerEntity
import net.stckoverflw.twitchcontrols.minecraft.EventManager

class TwitchEventsClient(player: PlayerEntity, target: String) {

    val twitch4jClient: TwitchClient = TwitchClientBuilder.builder()
        .withEnablePubSub(true)
        .withEnableHelix(true)
        .build()

    private var channelPointSubscription: PubSubSubscription

    init {
        twitch4jClient.pubSub.connect()
        channelPointSubscription = twitch4jClient.pubSub.listenForChannelPointsRedemptionEvents(null, target)

        EventManager.twitchEvents.forEach {
            it.runEvent(twitch4jClient.pubSub.eventManager, player)
        }
    }

    fun close() {
        twitch4jClient.pubSub.close()
    }


}