package net.stckoverflw.twitchcontrols.command

import net.axay.fabrik.commands.command

val mainCommand = command("tc") {
    menuCommand()
    connectCommand()
    reloadProfilesCommand()
    createProfileCommand()
    simulateCommand()
    stateCommand()
}