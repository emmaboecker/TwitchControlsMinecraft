package net.stckoverflw.twitchcontrols.minecraft.twitch.impl

import com.github.twitch4j.pubsub.events.ChannelSubscribeEvent
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
import net.stckoverflw.twitchcontrols.gui.selectActionGUI
import net.stckoverflw.twitchcontrols.minecraft.twitch.SubscribeEventData
import net.stckoverflw.twitchcontrols.minecraft.twitch.TwitchEvent
import net.stckoverflw.twitchcontrols.util.goBackButton
import net.stckoverflw.twitchcontrols.util.rangeChangerMax
import net.stckoverflw.twitchcontrols.util.rangeChangerMin

const val subscribeEventId = "subscribe"

object SubscribeEvent :
    TwitchEvent<SubscribeEventData, ChannelSubscribeEvent>(subscribeEventId, ChannelSubscribeEvent::class.java) {

    override val icon: ItemStack = itemStack(Items.DIAMOND, 1) {
        setCustomName("Subscription".literal.formatted(Formatting.BLUE))
    }

    override fun gui(player: PlayerEntity): Gui = igui(GuiType.NINE_BY_FIVE, "§9Subscription".literal, 1) {
        val monthsRangeProperty = GuiProperty(1..1)
        page(1, 1) {
            placeholder(Slots.All, grayPlaceholder)

            goBackButton()

            button(
                4 sl 3, GuiIcon.VariableIcon(
                    monthsRangeProperty,
                    monthsRangeProperty.guiIcon {
                        itemStack(Items.FEATHER, 1) {
                            setCustomName(
                                "Minimal Months: ".literal.formatted(Formatting.AQUA)
                                    .append(it.first.toString().literal.formatted(Formatting.BLUE))
                            )
                            setLore(
                                listOf(
                                    "The minimal amount of months required to trigger this action".literal.formatted(
                                        Formatting.GRAY
                                    ),
                                    "".literal,
                                    "Click to higher, shift click to lower".literal.formatted(Formatting.GRAY),
                                    "".literal,
                                    "Attention! ".literal.formatted(Formatting.RED).append(
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
                4 sl 7, GuiIcon.VariableIcon(
                    monthsRangeProperty,
                    monthsRangeProperty.guiIcon {
                        itemStack(Items.BOOK, 1) {
                            setCustomName(
                                "Maximal Months: ".literal.formatted(Formatting.AQUA)
                                    .append(it.last.toString().literal.formatted(Formatting.BLUE))
                            )
                            setLore(
                                listOf(
                                    "The maximal amount of months required to trigger this action".literal.formatted(
                                        Formatting.GRAY
                                    ),
                                    "".literal,
                                    "Click to higher, shift click to lower".literal.formatted(Formatting.GRAY),
                                    "".literal,
                                    "Attention! ".literal.formatted(Formatting.RED).append(
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

            button(2 sl 5, GuiIcon.VariableIcon(monthsRangeProperty, monthsRangeProperty.guiIcon {
                itemStack(Items.WRITABLE_BOOK, 1) {
                    setCustomName("Confirm".literal.formatted(Formatting.GREEN))
                    setLore(
                        listOf(
                            "with range = ${it.first.toString() + "<=..<=" + it.last.toString()}".literal.formatted(
                                Formatting.GRAY
                            )
                        )
                    )
                }
            }.iconGenerator)) {
                it.player.openGui(selectActionGUI(SubscribeEventData(monthsRangeProperty.get())), 1)
            }
        }
    }

    override fun runEvent(
        event: ChannelSubscribeEvent,
        eventData: SubscribeEventData,
        player: PlayerEntity
    ): Pair<Boolean, String> {
        if (!event.data.isGift) {
            val monthRange = eventData.requiredMonths
            return (monthRange == null || (monthRange.first <= event.data.cumulativeMonths && monthRange.last >= event.data.cumulativeMonths)) to event.data.userName
        }
        return false to event.data.displayName
    }

}