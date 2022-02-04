package net.stckoverflw.twitchcontrols.minecraft.twitch

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import com.github.twitch4j.pubsub.PubSubSubscription
import net.minecraft.entity.player.PlayerEntity
import net.stckoverflw.twitchcontrols.minecraft.EventManager

class TwitchEventsClient(player: PlayerEntity, target: String, token: String? = null) {

    val twitch4jClient: TwitchClient = TwitchClientBuilder.builder()
        .withDefaultAuthToken(OAuth2Credential("twitch", token))
        .withEnablePubSub(true)
        .withEnableHelix(true)
        .build()

    private var subscriptions = ArrayList<PubSubSubscription>()

    init {
        println("init event client")
        val credential = if (token != null) OAuth2Credential("twitch", token) else null
        twitch4jClient.pubSub.connect()
        println("connected pubsub")
        subscriptions += listOf(
            twitch4jClient.pubSub.listenForChannelPointsRedemptionEvents(credential, target),
            twitch4jClient.pubSub.listenForFollowingEvents(credential, target),
            twitch4jClient.pubSub.listenForSubscriptionEvents(credential, target),
            twitch4jClient.pubSub.listenForChannelSubGiftsEvents(credential, target),
            twitch4jClient.pubSub.listenForCheerEvents(credential, target),
        )

        println("sub topic")

        EventManager.twitchEvents.forEach {
            println(it)
            it.runEvent(twitch4jClient.pubSub.eventManager, player)
        }
    }

    fun close() {
        twitch4jClient.pubSub.close()
    }


}