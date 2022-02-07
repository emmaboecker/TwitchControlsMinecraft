package net.stckoverflw.twitchcontrols.command

import com.github.twitch4j.pubsub.domain.*
import com.github.twitch4j.pubsub.events.*
import com.mojang.brigadier.context.CommandContext
import net.axay.fabrik.commands.LiteralCommandBuilder
import net.axay.fabrik.core.text.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Formatting
import net.stckoverflw.twitchcontrols.twitchEventsClients
import java.time.Instant

private const val SimulationUser = "StckOverflw"
private const val SimulationId = "000000000"

fun LiteralCommandBuilder<ServerCommandSource>.simulateCommand() = literal("simulate") {
    try {
        simulateFollowCommand()
        simulateSubCommand()
        simulateSubGiftEventCommand()
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
                ChannelBitsEvent(ChannelBitsData().apply {
                    this.userId = SimulationId
                    this.userName = SimulationUser
                    this.bitsUsed = amountArg()
                })
            )
        }
    }
}

private fun LiteralCommandBuilder<ServerCommandSource>.simulateSubGiftEventCommand() = literal("sub-gifts") {
    argument<Int>("amount") { amountArg ->
        runs {
            simulateEvent(
                ChannelSubGiftEvent(SubGiftData().apply {
                    this.userId = SimulationId
                    this.userName = SimulationUser.lowercase()
                    this.displayName = SimulationUser
                    this.count = amountArg()
                })
            )
        }
    }
}

private fun LiteralCommandBuilder<ServerCommandSource>.simulateSubCommand() = literal("sub") {
    argument<Int>("months") { monthsArg ->
        runs {
            simulateEvent(
                ChannelSubscribeEvent(SubscriptionData().apply {
                    this.userId = SimulationId
                    this.userName = SimulationUser.lowercase()
                    this.displayName = SimulationUser
                    this.isGift = false
                    this.cumulativeMonths = monthsArg()
                    this.streakMonths = monthsArg()
                })
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
