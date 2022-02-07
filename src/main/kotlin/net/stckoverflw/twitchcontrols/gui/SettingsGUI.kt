package net.stckoverflw.twitchcontrols.gui

import kotlinx.coroutines.launch
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import net.axay.fabrik.core.item.itemStack
import net.axay.fabrik.core.item.setLore
import net.axay.fabrik.core.task.fabrikCoroutineScope
import net.axay.fabrik.core.text.literal
import net.axay.fabrik.igui.*
import net.axay.fabrik.igui.observable.toGuiList
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Formatting
import net.stckoverflw.twitchcontrols.gui.item.grayPlaceholder
import net.stckoverflw.twitchcontrols.minecraft.EventManager
import net.stckoverflw.twitchcontrols.minecraft.removeAction
import net.stckoverflw.twitchcontrols.util.JSON

fun settingsGUI(player: PlayerEntity): Gui =
    if (EventManager.activeProfile[player.uuid] != null) igui(GuiType.NINE_BY_FIVE, "§9Settings".literal, 1) {
        page(1, 1) {
            placeholder(Slots.All, grayPlaceholder)

            button(4 sl 2, itemStack(Items.NAME_TAG, 1) {
                setCustomName("Profile selector".literal.formatted(Formatting.BLUE))
            }.guiIcon) {
                it.player.openGui(profilesGUI(player), 1)
            }

            changePageByKey(2 sl 2, itemStack(Items.ITEM_FRAME, 1) {
                setCustomName("Add Action".literal.formatted(Formatting.GREEN))
            }.guiIcon, 3)

            val compound = compound(
                (1 sl 4) rectTo (5 sl 8),
                EventManager.activeProfile[player.uuid]!!.actions.map { it.key to it.value }.toGuiList(),
                iconGenerator = {
                    var itemStack: ItemStack = itemStack(Items.BARRIER, 1) {
                        setCustomName("There was an error loading this action".literal.formatted(Formatting.RED))
                    }
                    EventManager.twitchEvents.forEach { currentEvent ->
                        if (currentEvent.eventId == JSON.encodeToJsonElement(it.first).jsonObject["type"]
                                .toString().replace("\"", "")
                        ) {
                            itemStack = currentEvent.icon.copy().apply {
                                setLore(
                                    listOf(
                                        "§7with ${it.first}".literal,
                                        "§7do ${it.second}".literal,
                                        "".literal,
                                        "§7Click this to §cremove it §7from your actions".literal
                                    )
                                )
                            }
                        }
                    }
                    itemStack
                },
                onClick = { event, element ->
                    EventManager.activeProfile[event.player.uuid]?.removeAction(element.first)
                    event.player.sendMessage("Removed action".literal.formatted(Formatting.RED), false)
                    event.player.openGui(settingsGUI(event.player), 1)
                }
            )

            compoundScrollForwards(1 sl 9, itemStack(Items.NETHERITE_BLOCK, 1) {
                setCustomName("Scroll Forward".literal.formatted(Formatting.GREEN))
            }.guiIcon, compound)
            compoundScrollBackwards(2 sl 9, itemStack(Items.NETHERITE_BLOCK, 1) {
                setCustomName("Scroll Backwards".literal.formatted(Formatting.RED))
            }.guiIcon, compound)
        }
        page(3, 2) {
            placeholder(Slots.All, grayPlaceholder)

            changePageByKey(1 sl 5, itemStack(Items.KNOWLEDGE_BOOK, 1) {
                setCustomName("Go to main page".literal.formatted(Formatting.AQUA))
            }.guiIcon, 1)

            compound(
                (4 sl 2) rectTo (2 sl 8),
                EventManager.twitchEvents.toGuiList(),
                iconGenerator = {
                    it.icon
                },
                onClick = { event, element ->
                    fabrikCoroutineScope.launch {
                        player.openGui(element.gui(event.player), 1)
                    }
                }
            )
        }
    } else profilesGUI(player)
