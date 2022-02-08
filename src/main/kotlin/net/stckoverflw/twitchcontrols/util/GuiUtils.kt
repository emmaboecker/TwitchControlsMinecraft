package net.stckoverflw.twitchcontrols.util

import net.axay.fabrik.core.item.itemStack
import net.axay.fabrik.core.text.literal
import net.axay.fabrik.igui.*
import net.axay.fabrik.igui.events.GuiClickEvent
import net.axay.fabrik.igui.observable.GuiProperty
import net.minecraft.item.Items
import net.minecraft.text.MutableText
import net.minecraft.util.Formatting
import net.stckoverflw.twitchcontrols.gui.settingsGUI

suspend fun GuiClickEvent.rangeChangerMin(rangeProperty: GuiProperty<IntRange>) {
    val range = rangeProperty.get()
    if (type == GuiActionType.PICKUP) {
        if (range.first >= range.last) return
        rangeProperty.set((range.first + 1)..range.last)
    } else if (type == GuiActionType.SHIFT_CLICK) {
        if (range.first <= 0) return
        rangeProperty.set((range.first - 1)..range.last)
    }
}

suspend fun GuiClickEvent.rangeChangerMax(rangeProperty: GuiProperty<IntRange>) {
    val range = rangeProperty.get()
    if (type == GuiActionType.PICKUP) {
        rangeProperty.set(range.first..(range.last + 1))
    } else if (type == GuiActionType.SHIFT_CLICK) {
        if (range.last <= 0) return
        if (range.first >= range.last) {
            rangeProperty.set((range.first - 1) until range.last)
        } else {
            rangeProperty.set(range.first until range.last)
        }
    }
}

fun GuiBuilder.PageBuilder.goBackButton(slots: GuiSlotCompound = 1 sl 1, text: String = "Go to main page") =
    button(slots, itemStack(Items.KNOWLEDGE_BOOK, 1) {
        setCustomName(text.literal.formatted(Formatting.AQUA))
    }.guiIcon) {
        it.player.openGui(settingsGUI(it.player), 1)
    }

fun GuiBuilder.PageBuilder.compoundScrolls(
    compound: GuiCompound<*>,
    scrollForwardText: MutableText = "Scroll Forward".literal.formatted(Formatting.GREEN),
    scrollBackwardsText: MutableText = "Scroll Backwards".literal.formatted(Formatting.RED)
) {
    compoundScrollForwards(1 sl 9, itemStack(Items.NETHERITE_BLOCK, 1) {
        setCustomName(scrollForwardText)
    }.guiIcon, compound)
    compoundScrollBackwards(2 sl 9, itemStack(Items.NETHERITE_BLOCK, 1) {
        setCustomName(scrollBackwardsText)
    }.guiIcon, compound)
}
