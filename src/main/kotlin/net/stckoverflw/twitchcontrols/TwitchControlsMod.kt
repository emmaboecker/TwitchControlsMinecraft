package net.stckoverflw.twitchcontrols

import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.ModInitializer
import net.minecraft.entity.player.PlayerEntity
import net.stckoverflw.twitchcontrols.command.mainCommand
import net.stckoverflw.twitchcontrols.minecraft.EventManager
import net.stckoverflw.twitchcontrols.minecraft.twitch.TwitchEventsClient
import net.stckoverflw.twitchcontrols.util.twitchChannel
import net.stckoverflw.twitchcontrols.util.twitchToken
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.*

val twitchEventsClients = hashMapOf<UUID, TwitchEventsClient?>()

val twitchControlsLogger: Logger = LogManager.getLogger(MOD_ID)

fun createTwitchClient(player: PlayerEntity): TwitchEventsClient? {
    twitchEventsClients[player.uuid]?.close()
    twitchEventsClients[player.uuid] = null
    val twitchChannel = player.twitchChannel
    val twitchToken = player.twitchToken
    return if (twitchChannel != null && twitchToken != null) {
        twitchEventsClients[player.uuid] = TwitchEventsClient(player, twitchChannel, twitchToken)
        twitchEventsClients[player.uuid]
    } else {
        null
    }
}

class TwitchControlsMod : ModInitializer, DedicatedServerModInitializer {

    override fun onInitialize() {
        mainCommand
    }

    override fun onInitializeServer() {
        EventManager()
    }

}
