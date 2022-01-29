package net.stckoverflw.twitchcontrols.command

import net.axay.fabrik.commands.LiteralCommandBuilder
import net.axay.fabrik.core.text.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Formatting
import net.stckoverflw.twitchcontrols.minecraft.EventManager

fun LiteralCommandBuilder<ServerCommandSource>.reloadProfilesCommand() = literal("reload-profiles") {
    runs {
        EventManager.profiles = EventManager.loadProfiles()
        this.source.sendFeedback("Profiles reloaded".literal.formatted(Formatting.GREEN), false)
    }
}