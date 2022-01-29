package net.stckoverflw.twitchcontrols.minecraft.twitch

import net.axay.fabrik.igui.Gui
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import com.github.philippheuer.events4j.core.EventManager as TwitchEventManager

abstract class TwitchEvent<T : EventData>(val eventId: String) {

    abstract val icon: ItemStack

    abstract fun gui(player: PlayerEntity): Gui

    abstract fun runEvent(eventManager: TwitchEventManager, player: PlayerEntity)

}