package net.stckoverflw.twitchcontrols.minecraft.twitch

import com.github.twitch4j.common.events.TwitchEvent
import net.axay.fabrik.igui.Gui
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack

abstract class TwitchEvent<T : EventData, K : TwitchEvent>(val eventId: String, val eventClass: Class<K>) {

    abstract val icon: ItemStack

    abstract fun gui(player: PlayerEntity): Gui

    abstract fun runEvent(event: K, eventData: T, player: PlayerEntity): Pair<Boolean, String>

    @Suppress("UNCHECKED_CAST")
    fun runEventSafe(event: TwitchEvent, eventData: EventData, player: PlayerEntity): Pair<Boolean, String>? {
        return try {
            runEvent(event as K, eventData as T, player)
        } catch (_: ClassCastException) {
            null
        }

    }

}