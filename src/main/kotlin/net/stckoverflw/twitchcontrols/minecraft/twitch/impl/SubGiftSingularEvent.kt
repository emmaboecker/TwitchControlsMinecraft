package net.stckoverflw.twitchcontrols.minecraft.twitch.impl

import com.github.twitch4j.pubsub.events.ChannelSubGiftEvent
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
import net.stckoverflw.twitchcontrols.minecraft.twitch.SubGiftSingularEventData
import net.stckoverflw.twitchcontrols.minecraft.twitch.TwitchEvent
import net.stckoverflw.twitchcontrols.util.goBackButton
import net.stckoverflw.twitchcontrols.util.rangeChangerMax
import net.stckoverflw.twitchcontrols.util.rangeChangerMin

const val subGiftSingularEventId = "subscription-gift-single"

object SubGiftSingularEvent : TwitchEvent<SubGiftSingularEventData, ChannelSubGiftEvent>(
    subGiftSingularEventId,
    ChannelSubGiftEvent::class.java
) {
    override val icon: ItemStack = itemStack(Items.AMETHYST_SHARD, 1) {
        setCustomName("Sub gift (one action for all gifts)".literal.formatted(Formatting.AQUA))
    }

    override fun gui(player: PlayerEntity): Gui =
        igui(GuiType.NINE_BY_FIVE, "§9Sub gift (one action for all gifts)".literal, 1) {
            val useGifersNameProperty = GuiProperty(true)
            val amountRangeProperty = GuiProperty(1..1)
            page(1, 1) {
                placeholder(Slots.All, grayPlaceholder)

                goBackButton()

                button(
                    4 sl 3, GuiIcon.VariableIcon(
                        amountRangeProperty,
                        amountRangeProperty.guiIcon {
                            itemStack(Items.FEATHER, 1) {
                                setCustomName(
                                    "Minimal Gifts: ".literal.formatted(Formatting.AQUA)
                                        .append(it.first.toString().literal.formatted(Formatting.BLUE))
                                )
                                setLore(
                                    listOf(
                                        "The minimal amount of subs required to trigger this action".literal.formatted(
                                            Formatting.GRAY
                                        ),
                                        "".literal,
                                        "Click to higher, shift click to lower".literal.formatted(Formatting.GRAY),
                                        "".literal,
                                        "Attention! ".literal.formatted(Formatting.RED).append(
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
                    4 sl 7, GuiIcon.VariableIcon(
                        amountRangeProperty,
                        amountRangeProperty.guiIcon {
                            itemStack(Items.BOOK, 1) {
                                setCustomName(
                                    "Maximal Gifts: ".literal.formatted(Formatting.AQUA)
                                        .append(it.last.toString().literal.formatted(Formatting.BLUE))
                                )
                                setLore(
                                    listOf(
                                        "The maximal amount of subs required to trigger this action".literal.formatted(
                                            Formatting.GRAY
                                        ),
                                        "".literal,
                                        "Click to higher, shift click to lower".literal.formatted(Formatting.GRAY),
                                        "".literal,
                                        "Attention! ".literal.formatted(Formatting.RED).append(
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

                button(2 sl 4, GuiIcon.VariableIcon(useGifersNameProperty, useGifersNameProperty.guiIcon {
                    itemStack(Items.NAME_TAG, 1) {
                        setCustomName("Use Gifters username".literal.formatted(if (it) Formatting.GREEN else Formatting.RED))
                        setLore(
                            listOf(
                                "The Action will be triggered".literal.formatted(Formatting.GRAY).append(
                                    if (it) "with the gifters name".literal.formatted(
                                        Formatting.GREEN
                                    ) else "with the name of the receiving user".literal.formatted(Formatting.RED)
                                )
                            )
                        )
                    }
                }.iconGenerator)) {
                    useGifersNameProperty.set(!useGifersNameProperty.get())
                }

                button(2 sl 6, GuiIcon.VariableIcon(amountRangeProperty, amountRangeProperty.guiIcon {
                    itemStack(Items.WRITABLE_BOOK, 1) {
                        setCustomName("Confirm".literal.formatted(Formatting.GREEN))
                        setLore(
                            listOf(
                                "with rangle = ${it.first}<=..<=${it.last}".literal.formatted(Formatting.GRAY)
                            )
                        )
                    }
                }.iconGenerator)) {
                    it.player.openGui(
                        selectActionGUI(
                            SubGiftSingularEventData(
                                useGifersNameProperty.get(),
                                amountRangeProperty.get()
                            )
                        ), 1
                    )
                }
            }
        }

    override fun runEvent(
        event: ChannelSubGiftEvent,
        eventData: SubGiftSingularEventData,
        player: PlayerEntity
    ): Pair<Boolean, String> {
        val subGiftAmountRange = eventData.amountRange
        return (subGiftAmountRange == null || (subGiftAmountRange.first <= event.data.count && subGiftAmountRange.last >= event.data.count)) to if (eventData.useGifterName) event.data.displayName else event.data.displayName
    }
}