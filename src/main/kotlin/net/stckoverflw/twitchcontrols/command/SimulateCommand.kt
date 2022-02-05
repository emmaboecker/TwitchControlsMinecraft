package net.stckoverflw.twitchcontrols.command

import com.github.twitch4j.chat.events.channel.CheerEvent
import com.github.twitch4j.chat.events.channel.IRCMessageEvent
import com.github.twitch4j.chat.flag.AutoModFlag
import com.github.twitch4j.common.events.domain.EventChannel
import com.github.twitch4j.common.events.domain.EventUser
import com.github.twitch4j.pubsub.domain.ChannelPointsRedemption
import com.github.twitch4j.pubsub.domain.ChannelPointsReward
import com.github.twitch4j.pubsub.domain.ChannelPointsUser
import com.github.twitch4j.pubsub.domain.FollowingData
import com.github.twitch4j.pubsub.events.FollowingEvent
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent
import com.mojang.brigadier.context.CommandContext
import net.axay.fabrik.commands.LiteralCommandBuilder
import net.axay.fabrik.core.text.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Formatting
import net.stckoverflw.twitchcontrols.twitchEventsClients
import net.stckoverflw.twitchcontrols.util.twitchChannel
import java.time.Instant

private const val SimulationUser = "StckOverflw"
private const val SimulationId = "000000000"

fun LiteralCommandBuilder<ServerCommandSource>.simulateCommand() = literal("simulate") {
    try {
        simulateFollowCommand()
        simulateChannelPointsCommand()
        simulateCheerBitsCommand()
    } catch (ex: Exception) {
        ex.printStackTrace()
    }

}

private fun LiteralCommandBuilder<ServerCommandSource>.simulateFollowCommand() = literal("follow") {
    runs {
        simulateEvent(FollowingEvent(
            SimulationId,
            FollowingData().apply {
                this.userId = SimulationId
                this.displayName = SimulationUser
                this.username = SimulationUser.lowercase()
            }
        ))
    }
}

private fun LiteralCommandBuilder<ServerCommandSource>.simulateChannelPointsCommand() = literal("channel-points") {
    argument<String>("title") { titleArg ->
        runs {
            simulateEvent(
                RewardRedeemedEvent(
                    Instant.now(),
                    ChannelPointsRedemption().apply {
                        this.reward = ChannelPointsReward().apply {
                            this.cost = 100
                            this.title = titleArg()
                        }
                        this.user = ChannelPointsUser().apply {
                            this.id = SimulationId
                            this.displayName = SimulationUser
                            this.login = SimulationUser.lowercase()
                        }
                    }
                )
            )
        }
    }
}

private fun LiteralCommandBuilder<ServerCommandSource>.simulateCheerBitsCommand() = literal("bits") {
    argument<Int>("amount") { amountArg ->
        runs {
            simulateEvent(
                CheerEvent(
                    IRCMessageEvent(
                        "take some pennys",
                        mapOf(source.player.twitchChannel to source.player.name.string),
                        mapOf(source.player.name.string to source.player.twitchChannel),
                        listOf(SimulationId)
                    ),
                    EventChannel(source.player.twitchChannel, source.player.name.string),
                    EventUser(SimulationId, SimulationUser),
                    "take some pennys",
                    amountArg(),
                    0,
                    1,
                    listOf(AutoModFlag.builder().build())
                )
            )
        }
    }
}

private fun CommandContext<ServerCommandSource>.simulateEvent(event: Any) {
    val twitchEventClient = twitchEventsClients[this.source.player.uuid]
    if (twitchEventClient != null) {
        twitchEventClient.twitch4jClient.pubSub.eventManager.publish(event)
    } else {
        source.sendError("You haven't set a twitch channel yet".literal.formatted(Formatting.RED))
    }
}
