package net.stckoverflw.twitchcontrols.minecraft.action.impl

import net.axay.fabrik.core.item.itemStack
import net.axay.fabrik.core.text.literal
import net.axay.fabrik.igui.Gui
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Formatting
import net.stckoverflw.twitchcontrols.minecraft.action.Action
import net.stckoverflw.twitchcontrols.minecraft.action.DamageData
import net.stckoverflw.twitchcontrols.minecraft.action.TwitchExecutorData
import net.stckoverflw.twitchcontrols.minecraft.twitch.EventData

const val damageActionId = "damage"

class KillAction : Action<DamageData>(damageActionId) {

    override val icon: ItemStack = itemStack(Items.IRON_SWORD, 1) {
        setCustomName("Kill Player".literal.formatted(Formatting.RED))
    }

    override fun run(player: PlayerEntity, twitchData: TwitchExecutorData, data: DamageData) {
        player.damage(DamageSource.GENERIC, data.damage.toFloat())
    }

    override fun gui(eventData: EventData): Gui? = null

    override fun getActionData(): DamageData = DamageData(20)

}