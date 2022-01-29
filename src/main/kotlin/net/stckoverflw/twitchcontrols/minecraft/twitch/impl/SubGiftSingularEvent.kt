package net.stckoverflw.twitchcontrols.minecraft.twitch.impl

import com.github.twitch4j.chat.events.channel.SubscriptionEvent
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import net.axay.fabrik.core.item.itemStack
import net.axay.fabrik.core.text.literal
import net.axay.fabrik.igui.Gui
import net.axay.fabrik.igui.GuiType
import net.axay.fabrik.igui.igui
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Formatting
import net.stckoverflw.twitchcontrols.minecraft.EventManager
import net.stckoverflw.twitchcontrols.minecraft.action.TwitchExecutorData
import net.stckoverflw.twitchcontrols.minecraft.twitch.SubGiftSingularEventData
import net.stckoverflw.twitchcontrols.minecraft.twitch.TwitchEvent
import net.stckoverflw.twitchcontrols.util.JSON
import com.github.philippheuer.events4j.core.EventManager as TwitchEventManager

const val subGiftSingularEventId = "subscription-gift-single"

object SubGiftSingularEvent : TwitchEvent<SubGiftSingularEventData>(subGiftSingularEventId) {
    override val icon: ItemStack = itemStack(Items.AMETHYST_SHARD, 1) {
        setCustomName("Sub gift (Action for every sub)".literal.formatted(Formatting.AQUA))
    }

    override fun gui(player: PlayerEntity): Gui =
        igui(GuiType.NINE_BY_FIVE, "§9Sub gift (Action for every sub)".literal, 1) {
            page(1, 1) {

            }
        }

    override fun runEvent(eventManager: TwitchEventManager, player: PlayerEntity) {
        eventManager.onEvent(SubscriptionEvent::class.java) {
            val activeProfile = EventManager.activeProfile[player.uuid] ?: return@onEvent
            activeProfile.actions.forEach { (eventData, actionData) ->
                EventManager.actions.forEach { currentAction ->
                    if (currentAction.actionId == JSON.encodeToJsonElement(actionData).jsonObject["action"]
                            .toString().replace("\"", "")
                    ) {
                        if (eventData !is SubGiftSingularEventData) return@onEvent
                        if (it.gifted) {
                            currentAction.runSafe(
                                player,
                                TwitchExecutorData(if (eventData.useGifterName) it.giftedBy.name else it.user.name),
                                actionData
                            )
                        }
                    }
                }
            }
        }
    }
}