package net.stckoverflw.twitchcontrols.command

import net.axay.fabrik.commands.LiteralCommandBuilder
import net.axay.fabrik.igui.openGui
import net.minecraft.server.command.ServerCommandSource
import net.stckoverflw.twitchcontrols.gui.profilesGUI
import net.stckoverflw.twitchcontrols.gui.settingsGUI
import net.stckoverflw.twitchcontrols.minecraft.EventManager

fun LiteralCommandBuilder<ServerCommandSource>.menuCommand() = literal("menu") {
    runs {
        if (EventManager.activeProfile[source.player.uuid] != null) {
            source.player.openGui(settingsGUI(source.player), 1)
        } else {
            source.player.openGui(profilesGUI(source.player), 1)
        }
    }
}