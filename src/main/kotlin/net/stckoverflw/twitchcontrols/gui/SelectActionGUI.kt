package net.stckoverflw.twitchcontrols.gui

import net.axay.fabrik.core.text.literal
import net.axay.fabrik.igui.*
import net.axay.fabrik.igui.observable.toGuiList
import net.stckoverflw.twitchcontrols.gui.item.grayPlaceholder
import net.stckoverflw.twitchcontrols.minecraft.EventManager
import net.stckoverflw.twitchcontrols.minecraft.addAction
import net.stckoverflw.twitchcontrols.minecraft.twitch.EventData

fun selectActionGUI(eventData: EventData) = igui(GuiType.NINE_BY_FIVE, "§6Select an Action".literal, 1) {
    page(1, 1) {
        placeholder(Slots.All, grayPlaceholder)

        compound(
            (2 sl 2) rectTo (4 sl 8),
            EventManager.actions.toGuiList(),
            iconGenerator = { it.icon },
            onClick = { event, element ->
                val activeProfile = EventManager.activeProfile[event.player.uuid] ?: return@compound
                val gui = element.gui(eventData)
                if (gui == null) {
                    activeProfile.addAction(
                        eventData, element.getActionData()
                            ?: error("getActionData is required for $element because there is no custom gui")
                    )
                    event.player.openGui(settingsGUI(event.player), 1)
                } else {
                    event.player.openGui(gui, 1)
                }
            }
        )
    }
}