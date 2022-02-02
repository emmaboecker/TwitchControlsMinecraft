package net.stckoverflw.twitchcontrols.command

import net.axay.fabrik.commands.LiteralCommandBuilder
import net.axay.fabrik.core.text.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Formatting
import net.stckoverflw.twitchcontrols.minecraft.EventManager
import net.stckoverflw.twitchcontrols.minecraft.Profile

fun LiteralCommandBuilder<ServerCommandSource>.createProfileCommand() = literal("create-profile") {
    argument<String>("name") { profileName ->
        runs {
            try {
                val name = profileName().lowercase()
                if (EventManager.profiles.any { it.name.lowercase() == name }) {
                    source.sendError("There is already a profile with that name".literal.formatted(Formatting.RED))
                    return@runs
                }
                EventManager.profiles += Profile(
                    name = name,
                    player = source.player.uuidAsString,
                    actions = hashMapOf()
                )
                EventManager.saveProfiles()
                source.sendFeedback("Created profile $name".literal.formatted(Formatting.GREEN), false)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}