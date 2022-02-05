package net.stckoverflw.twitchcontrols.minecraft.action.impl

import net.axay.fabrik.core.item.itemStack
import net.axay.fabrik.core.text.literal
import net.axay.fabrik.igui.Gui
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Formatting
import net.stckoverflw.twitchcontrols.minecraft.action.Action
import net.stckoverflw.twitchcontrols.minecraft.action.TpUpActionData
import net.stckoverflw.twitchcontrols.minecraft.action.TwitchExecutorData
import net.stckoverflw.twitchcontrols.minecraft.twitch.EventData

const val tpUpActionId = "tp-up"

class TpUpAction : Action<TpUpActionData>(tpUpActionId) {
    override val icon: ItemStack = itemStack(Items.IRON_BOOTS, 1) {
        setCustomName("Teleport 200 blocks up".literal.formatted(Formatting.AQUA))
    }

    override fun run(player: PlayerEntity, twitchData: TwitchExecutorData, data: TpUpActionData) {
        player.teleport(player.x, player.y + data.amount, player.z)
    }

    override fun gui(eventData: EventData): Gui? = null

    override fun getActionData(): TpUpActionData = TpUpActionData(200)

}
