package net.stckoverflw.twitchcontrols.command

import net.axay.fabrik.commands.LiteralCommandBuilder
import net.axay.fabrik.core.text.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Formatting
import net.stckoverflw.twitchcontrols.createTwitchClient
import net.stckoverflw.twitchcontrols.twitchEventsClients

fun LiteralCommandBuilder<ServerCommandSource>.stateCommand() {
    literal("start") {
        runs {
            if (twitchEventsClients[source.player.uuid] == null && createTwitchClient(source.player) != null) {
                source.player.sendMessage(
                    "Twitch Controls Minecraft by StckOverflw was started".literal.formatted(
                        Formatting.GREEN
                    ), false
                )
            } else {
                source.player.sendMessage(
                    "Couldn't connect Twitch, you probably didn't set you channel/token correctly yet".literal.formatted(
                        Formatting.RED
                    ), false
                )
            }
        }
    }
    literal("stop") {
        runs {
            if (twitchEventsClients[source.player.uuid] != null) {
                twitchEventsClients[source.player.uuid]?.close()
                twitchEventsClients[source.player.uuid] = null
                source.player.sendMessage(
                    "Twitch Controls Minecraft was stopped".literal.formatted(Formatting.GREEN),
                    false
                )
            } else {
                source.player.sendMessage(
                    "Twitch Controls Minecraft is not running".literal.formatted(Formatting.RED),
                    false
                )
            }
        }
    }
}