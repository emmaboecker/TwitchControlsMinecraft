package net.stckoverflw.twitchcontrols.gui

import net.axay.fabrik.core.item.itemStack
import net.axay.fabrik.core.item.setLore
import net.axay.fabrik.core.text.literal
import net.axay.fabrik.igui.*
import net.axay.fabrik.igui.observable.GuiProperty
import net.axay.fabrik.igui.observable.toGuiList
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.util.Formatting
import net.stckoverflw.twitchcontrols.gui.item.grayPlaceholder
import net.stckoverflw.twitchcontrols.minecraft.EventManager

fun profilesGUI(player: PlayerEntity) = igui(GuiType.NINE_BY_FIVE, "§9Profiles".literal, 1) {
    page(1, 1) {
        placeholder(Slots.All, grayPlaceholder)

        if (EventManager.activeProfile[player.uuid] != null) {
            button(2 sl 2, itemStack(Items.KNOWLEDGE_BOOK, 1) {
                setCustomName("Go to main page".literal.formatted(Formatting.AQUA))
            }.guiIcon) {
                it.player.openGui(settingsGUI(player), 1)
            }
        }

        placeholder(4 sl 2, GuiIcon.VariableIcon(GuiProperty(EventManager.activeProfile)) {
            if (it[player.uuid] != null) {
                itemStack(Items.NAME_TAG, 1) {
                    setCustomName(
                        ("Current Profile: ".literal.formatted(Formatting.BLUE)
                            .append(it[player.uuid]!!.name.literal.formatted(Formatting.AQUA)))
                    )
                }
            } else {
                itemStack(Items.BARRIER, 1) {
                    setCustomName("No Profile selected".literal.formatted(Formatting.RED))
                    setLore(
                        listOf(
                            "§7Use §9/tc create-profile [name] §7to create a Profile".literal
                        )
                    )
                }
            }
        })

        compound(
            (2 sl 4) rectTo (4 sl 8),
            EventManager.getProfilesForPlayer(player).toGuiList(),
            iconGenerator = {
                itemStack(Items.NAME_TAG, 1) {
                    setCustomName(it.name.literal)
                }
            },
            onClick = { event, element ->
                EventManager.activeProfile[event.player.uuid] = element
                event.player.openGui(settingsGUI(event.player), 1)
            }
        )
    }
}