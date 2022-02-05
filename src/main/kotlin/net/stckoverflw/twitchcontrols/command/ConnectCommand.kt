package net.stckoverflw.twitchcontrols.command

import net.axay.fabrik.commands.LiteralCommandBuilder
import net.axay.fabrik.core.text.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Formatting
import net.stckoverflw.twitchcontrols.util.twitchChannel
import net.stckoverflw.twitchcontrols.util.twitchToken


fun LiteralCommandBuilder<ServerCommandSource>.connectCommand() = literal("connect") {
    literal("token") {
        argument<String>("twitch-token") { twitchToken ->
            runs {
                source.player.twitchToken = twitchToken()
                source.sendFeedback("Twitch Token successfully set".literal.formatted(Formatting.GREEN), false)
            }
        }
    }
    literal("channel") {
        argument<String>("twitch-channel") { twitchChannel ->
            runs {
                val twitchChannelName = twitchChannel()
                source.player.twitchChannel = twitchChannelName
                source.sendFeedback("Set channel to $twitchChannelName".literal.formatted(Formatting.GREEN), false)
            }
        }
    }
}
