package net.stckoverflw.twitchcontrols.minecraft.action.impl

import net.axay.fabrik.core.item.itemStack
import net.axay.fabrik.core.text.literal
import net.axay.fabrik.igui.Gui
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Formatting
import net.stckoverflw.twitchcontrols.minecraft.action.Action
import net.stckoverflw.twitchcontrols.minecraft.action.ClearInventoryData
import net.stckoverflw.twitchcontrols.minecraft.action.TwitchExecutorData
import net.stckoverflw.twitchcontrols.minecraft.twitch.EventData

const val clearInventoryId = "clear-inventory"

class ClearInventoryAction : Action<ClearInventoryData>(clearInventoryId) {
    override val icon: ItemStack = itemStack(Items.ITEM_FRAME, 1) {
        setCustomName("Clear Inventory".literal.formatted(Formatting.WHITE))
    }

    override fun run(player: PlayerEntity, twitchData: TwitchExecutorData, data: ClearInventoryData) {
        player.inventory.clear()
    }

    override fun gui(eventData: EventData): Gui? = null

    override fun getActionData(): ClearInventoryData = ClearInventoryData()

}