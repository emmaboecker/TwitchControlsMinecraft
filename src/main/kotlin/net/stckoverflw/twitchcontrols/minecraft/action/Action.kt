package net.stckoverflw.twitchcontrols.minecraft.action

import net.axay.fabrik.igui.Gui
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.stckoverflw.twitchcontrols.minecraft.twitch.EventData
import net.stckoverflw.twitchcontrols.twitchControlsLogger

abstract class Action<T : ActionData>(val actionId: String) {

    abstract val icon: ItemStack

    @Suppress("UNCHECKED_CAST")
    fun runSafe(player: PlayerEntity, twitchData: TwitchExecutorData, data: ActionData) {
        try {
            run(player, twitchData, data as T)
        } catch (ex: ClassCastException) {
            twitchControlsLogger.error("Not given the right data")
            twitchControlsLogger.error(ex.message)
        }
    }

    abstract fun run(player: PlayerEntity, twitchData: TwitchExecutorData, data: T)

    abstract fun gui(eventData: EventData): Gui?

    abstract fun getActionData(): T?

}

data class TwitchExecutorData(
    val executorName: String
)
