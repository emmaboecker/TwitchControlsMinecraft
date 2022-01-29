package net.stckoverflw.twitchcontrols.minecraft.twitch.impl

import com.github.twitch4j.chat.events.channel.SubscriptionEvent
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
import net.stckoverflw.twitchcontrols.minecraft.twitch.SubscribeEventData
import net.stckoverflw.twitchcontrols.minecraft.twitch.TwitchEvent
import net.stckoverflw.twitchcontrols.util.JSON
import net.stckoverflw.twitchcontrols.util.rangeChangerMax
import net.stckoverflw.twitchcontrols.util.rangeChangerMin
import com.github.philippheuer.events4j.core.EventManager as TwitchEventManager

const val subscribeEventId = "subscribe"

object SubscribeEvent : TwitchEvent<SubscribeEventData>(subscribeEventId) {

    override val icon: ItemStack = itemStack(Items.DIAMOND, 1) {
        setCustomName("Subscription".literal.formatted(Formatting.BLUE))
    }

    override fun gui(player: PlayerEntity): Gui = igui(GuiType.NINE_BY_FIVE, "§9Subscription".literal, 1) {
        val monthsRangeProperty = GuiProperty(0..0)
        page(1, 1) {
            placeholder(Slots.All, grayPlaceholder)

            button(
                4 sl 3, GuiIcon.VariableIcon(
                    monthsRangeProperty,
                    monthsRangeProperty.guiIcon {
                        itemStack(Items.FEATHER, 1) {
                            setCustomName("Minimal Months".literal.formatted(Formatting.AQUA))
                            setLore(
                                listOf(
                                    "The minimal amount of months required to trigger this action".literal.formatted(
                                        Formatting.GRAY
                                    ),
                                    "".literal,
                                    "Attention!".literal.formatted(Formatting.RED).append(
                                        "If there are multiple actions crossing the same range of months,".literal.formatted(
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
                it.rangeChangerMin(monthsRangeProperty)
            }

            button(
                4 sl 3, GuiIcon.VariableIcon(
                    monthsRangeProperty,
                    monthsRangeProperty.guiIcon {
                        itemStack(Items.BOOK, 1) {
                            setCustomName("Maximal Months".literal.formatted(Formatting.AQUA))
                            setLore(
                                listOf(
                                    "The maximal amount of months required to trigger this action".literal.formatted(
                                        Formatting.GRAY
                                    ),
                                    "".literal,
                                    "Click to higher, shift click to lower".literal.formatted(Formatting.GRAY),
                                    "".literal,
                                    "Attention!".literal.formatted(Formatting.RED).append(
                                        "If there are multiple actions crossing the same range of months,".literal.formatted(
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
                it.rangeChangerMax(monthsRangeProperty)
            }
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
                        if (eventData !is SubscribeEventData) return@onEvent
                        if (!it.gifted) {
                            val monthRange = eventData.requiredMonths
                            if (monthRange != null) {
                                if (!(monthRange.first <= it.months && monthRange.last >= it.months)) return@onEvent
                            }
                            currentAction.runSafe(
                                player,
                                TwitchExecutorData(it.user.name),
                                actionData
                            )
                        }
                    }
                }
            }
        }
    }

}