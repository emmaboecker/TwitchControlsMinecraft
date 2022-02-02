package net.stckoverflw.twitchcontrols.minecraft.action.impl

import net.axay.fabrik.core.item.itemStack
import net.axay.fabrik.core.text.literal
import net.axay.fabrik.igui.Gui
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Formatting
import net.stckoverflw.twitchcontrols.minecraft.action.Action
import net.stckoverflw.twitchcontrols.minecraft.action.LitPlayerOnFireData
import net.stckoverflw.twitchcontrols.minecraft.action.TwitchExecutorData
import net.stckoverflw.twitchcontrols.minecraft.twitch.EventData

const val litPlayerOnFireId = "lit-player-on-fire"

class LitPlayerOnFireAction : Action<LitPlayerOnFireData>(litPlayerOnFireId) {
    override val icon: ItemStack = itemStack(Items.FLINT_AND_STEEL, 1) {
        setCustomName("Lit Player On Fire".literal.formatted(Formatting.RED))
    }

    override fun run(player: PlayerEntity, twitchData: TwitchExecutorData, data: LitPlayerOnFireData) {
        player.fireTicks += (data.periodInSeconds * 20)
    }

    override fun gui(eventData: EventData): Gui? = null

    override fun getActionData(): LitPlayerOnFireData = LitPlayerOnFireData(5)
}