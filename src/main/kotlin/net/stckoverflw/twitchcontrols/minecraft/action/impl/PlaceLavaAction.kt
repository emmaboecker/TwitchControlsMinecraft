package net.stckoverflw.twitchcontrols.minecraft.action.impl

import net.axay.fabrik.core.item.itemStack
import net.axay.fabrik.core.text.literal
import net.axay.fabrik.igui.Gui
import net.minecraft.block.Blocks
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Formatting
import net.minecraft.util.registry.Registry
import net.stckoverflw.twitchcontrols.minecraft.action.Action
import net.stckoverflw.twitchcontrols.minecraft.action.PlaceBlockData
import net.stckoverflw.twitchcontrols.minecraft.action.TwitchExecutorData
import net.stckoverflw.twitchcontrols.minecraft.twitch.EventData

const val placeBlockActionId = "place-block"

class PlaceLavaAction : Action<PlaceBlockData>(placeBlockActionId) {
    override val icon: ItemStack = itemStack(Items.LAVA_BUCKET, 1) {
        setCustomName("Place Lava".literal.formatted(Formatting.RED))
    }

    override fun run(player: PlayerEntity, twitchData: TwitchExecutorData, data: PlaceBlockData) {
        player.world.setBlockState(player.blockPos, Registry.BLOCK.get(data.block).defaultState)
    }

    override fun gui(eventData: EventData): Gui? = null

    override fun getActionData(): PlaceBlockData = PlaceBlockData(Registry.BLOCK.getId(Blocks.LAVA))
}