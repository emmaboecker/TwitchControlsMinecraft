package net.stckoverflw.twitchcontrols.minecraft.action.impl

import net.axay.fabrik.core.item.itemStack
import net.axay.fabrik.core.text.literal
import net.axay.fabrik.igui.Gui
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Formatting
import net.stckoverflw.twitchcontrols.minecraft.action.Action
import net.stckoverflw.twitchcontrols.minecraft.action.RandomTeleportData
import net.stckoverflw.twitchcontrols.minecraft.action.TwitchExecutorData
import net.stckoverflw.twitchcontrols.minecraft.twitch.EventData

const val randomTeleportActionId = "random-teleport"

class RandomTeleportAction : Action<RandomTeleportData>(randomTeleportActionId) {
    override val icon: ItemStack = itemStack(Items.ENDER_PEARL, 1) {
        setCustomName("Random Teleport in 500 blocks radius".literal.formatted(Formatting.GOLD))
    }

    override fun run(player: PlayerEntity, twitchData: TwitchExecutorData, data: RandomTeleportData) {
        val server = MinecraftClient.getInstance().server ?: error("Server is null")
        server.commandManager.dispatcher.execute(
            "spreadplayers ~ ~ ${data.radius} ${data.radius} true ${player.name.string}",
            server.commandSource
        )
    }

    override fun gui(eventData: EventData): Gui? = null

    override fun getActionData(): RandomTeleportData = RandomTeleportData(500)

}
