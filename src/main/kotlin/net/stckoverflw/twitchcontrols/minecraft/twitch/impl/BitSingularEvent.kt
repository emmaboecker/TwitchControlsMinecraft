package net.stckoverflw.twitchcontrols.minecraft.twitch.impl

import com.github.twitch4j.chat.events.channel.CheerEvent
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
import net.stckoverflw.twitchcontrols.gui.selectActionGUI
import net.stckoverflw.twitchcontrols.minecraft.EventManager
import net.stckoverflw.twitchcontrols.minecraft.action.TwitchExecutorData
import net.stckoverflw.twitchcontrols.minecraft.twitch.BitMultipleEventData
import net.stckoverflw.twitchcontrols.minecraft.twitch.BitSingularEventData
import net.stckoverflw.twitchcontrols.minecraft.twitch.TwitchEvent
import net.stckoverflw.twitchcontrols.util.JSON
import net.stckoverflw.twitchcontrols.util.rangeChangerMax
import net.stckoverflw.twitchcontrols.util.rangeChangerMin
import com.github.philippheuer.events4j.core.EventManager as TwitchEventManager

const val bitSingularEventId = "bit-cheer-single"

object BitSingularEvent : TwitchEvent<BitSingularEventData>(bitSingularEventId) {
    override val icon: ItemStack = itemStack(Items.BRICK, 1) {
        setCustomName("Bits cheered (Action is repeated for every bit)".literal.formatted(Formatting.AQUA))
    }

    override fun gui(player: PlayerEntity): Gui =
        igui(GuiType.NINE_BY_FIVE, "§9Bit cheer (Action for every bit)".literal, 1) {
            val amountRangeProperty = GuiProperty(1..1)
            page(1, 1) {
                placeholder(Slots.All, grayPlaceholder)

                button(
                    4 sl 3, GuiIcon.VariableIcon(
                        amountRangeProperty,
                        amountRangeProperty.guiIcon {
                            itemStack(Items.FEATHER, 1) {
                                setCustomName(
                                    "Minimal Bits: ".literal.formatted(Formatting.AQUA)
                                        .append(it.first.toString().literal.formatted(Formatting.BLUE))
                                )
                                setLore(
                                    listOf(
                                        "The minimal amount of bits required to trigger this action".literal.formatted(
                                            Formatting.GRAY
                                        ),
                                        "".literal,
                                        "Click to higher, shift click to lower".literal.formatted(Formatting.GRAY),
                                        "".literal,
                                        "Attention! ".literal.formatted(Formatting.RED).append(
                                            "If there are multiple actions crossing the same range of bits,".literal.formatted(
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
                                    "Maximal Bits: ".literal.formatted(Formatting.AQUA)
                                        .append(it.last.toString().literal.formatted(Formatting.BLUE))
                                )
                                setLore(
                                    listOf(
                                        "The maximal amount of bits required to trigger this action".literal.formatted(
                                            Formatting.GRAY
                                        ),
                                        "".literal,
                                        "Click to higher, shift click to lower".literal.formatted(Formatting.GRAY),
                                        "".literal,
                                        "Attention! ".literal.formatted(Formatting.RED).append(
                                            "If there are multiple actions crossing the same range of bits,".literal.formatted(
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


                button(2 sl 5, GuiIcon.VariableIcon(amountRangeProperty, amountRangeProperty.guiIcon {
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
                    it.player.openGui(selectActionGUI(BitMultipleEventData(amountRangeProperty.get())), 1)
                }
            }
        }

    override fun runEvent(eventManager: TwitchEventManager, player: PlayerEntity) {
        eventManager.onEvent(CheerEvent::class.java) {
            val activeProfile = EventManager.activeProfile[player.uuid] ?: return@onEvent
            activeProfile.actions.forEach { (_, actionData) ->
                EventManager.actions.forEach { currentAction ->
                    if (currentAction.actionId == JSON.encodeToJsonElement(actionData).jsonObject["action"]
                            .toString().replace("\"", "")
                    ) {
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