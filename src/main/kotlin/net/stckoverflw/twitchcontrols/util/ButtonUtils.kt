package net.stckoverflw.twitchcontrols.util

import net.axay.fabrik.igui.GuiActionType
import net.axay.fabrik.igui.events.GuiClickEvent
import net.axay.fabrik.igui.observable.GuiProperty

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
