package net.stckoverflw.twitchcontrols.minecraft.twitch.impl

import com.github.twitch4j.eventsub.events.ChannelSubscriptionGiftEvent
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import net.axay.fabrik.core.item.itemStack
import net.axay.fabrik.core.item.setLore
import net.axay.fabrik.core.text.literal
import net.axay.fabrik.igui.*
import net.axay.fabrik.igui.observable.GuiProperty
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Formatting
import net.stckoverflw.twitchcontrols.gui.item.grayPlaceholder
import net.stckoverflw.twitchcontrols.minecraft.EventManager
import net.stckoverflw.twitchcontrols.minecraft.action.TwitchExecutorData
import net.stckoverflw.twitchcontrols.minecraft.twitch.SubGiftMultipleEventData
import net.stckoverflw.twitchcontrols.minecraft.twitch.SubGiftSingularEventData
import net.stckoverflw.twitchcontrols.minecraft.twitch.TwitchEvent
import net.stckoverflw.twitchcontrols.util.JSON
import net.stckoverflw.twitchcontrols.util.rangeChangerMax
import net.stckoverflw.twitchcontrols.util.rangeChangerMin
import com.github.philippheuer.events4j.core.EventManager as TwitchEventManager

const val subGiftMultipleEventId = "subscription-gift-multiple"

object SubGiftMultipleEvent : TwitchEvent<SubGiftSingularEventData>(subGiftMultipleEventId) {
    override val icon: ItemStack = itemStack(Items.AMETHYST_SHARD, 1) {
        setCustomName("Sub gift (one action for all gifts)".literal.formatted(Formatting.AQUA))
    }

    override fun gui(player: PlayerEntity): Gui =
        igui(GuiType.NINE_BY_FIVE, "§9Sub gift (Action for every sub)".literal, 1) {
            val amountRangeProperty = GuiProperty(0..0)
            page(1, 1) {
                placeholder(Slots.All, grayPlaceholder)

                button(
                    4 sl 3, GuiIcon.VariableIcon(
                        amountRangeProperty,
                        amountRangeProperty.guiIcon {
                            itemStack(Items.FEATHER, 1) {
                                setCustomName("Minimal Gifts".literal.formatted(Formatting.AQUA))
                                setLore(
                                    listOf(
                                        "The minimal amount of subs required to trigger this action".literal.formatted(
                                            Formatting.GRAY
                                        ),
                                        "".literal,
                                        "Attention!".literal.formatted(Formatting.RED).append(
                                            "If there are multiple actions crossing the same range of subs,".literal.formatted(
                                                Formatting.GRAY
                                            )
                                        ),
                                        "all actions will be triggered".literal.formatted(Formatting.RED)
                                    )
                                )
                            }
                        }.iconGenerator
                    )
                ) {
                    it.rangeChangerMin(amountRangeProperty)
                }

                button(
                    4 sl 3, GuiIcon.VariableIcon(
                        amountRangeProperty,
                        amountRangeProperty.guiIcon {
                            itemStack(Items.BOOK, 1) {
                                setCustomName("Maximal Gifts".literal.formatted(Formatting.AQUA))
                                setLore(
                                    listOf(
                                        "The maximal amount of subs required to trigger this action".literal.formatted(
                                            Formatting.GRAY
                                        ),
                                        "".literal,
                                        "Click to higher, shift click to lower".literal.formatted(Formatting.GRAY),
                                        "".literal,
                                        "Attention!".literal.formatted(Formatting.RED).append(
                                            "If there are multiple actions crossing the same range of subs,".literal.formatted(
                                                Formatting.GRAY
                                            )
                                        ),
                                        "all actions will be triggered".literal.formatted(Formatting.RED)
                                    )
                                )
                            }
                        }.iconGenerator
                    )
                ) {
                    it.rangeChangerMax(amountRangeProperty)
                }
            }
        }

    override fun runEvent(eventManager: TwitchEventManager, player: PlayerEntity) {
        eventManager.onEvent(ChannelSubscriptionGiftEvent::class.java) {
            val activeProfile = EventManager.activeProfile[player.uuid] ?: return@onEvent
            activeProfile.actions.forEach { (eventData, actionData) ->
                EventManager.actions.forEach { currentAction ->
                    if (currentAction.actionId == JSON.encodeToJsonElement(actionData).jsonObject["action"]
                            .toString().replace("\"", "")
                    ) {
                        if (eventData !is SubGiftMultipleEventData) return@onEvent
                        val subGiftAmountRange = eventData.amountRange
                        if (subGiftAmountRange != null) {
                            if (!(subGiftAmountRange.first <= it.total && subGiftAmountRange.last >= it.total)) return@onEvent
                        }
                        currentAction.runSafe(
                            player,
                            TwitchExecutorData(it.userName),
                            actionData
                        )

                    }
                }
            }
        }
    }
}