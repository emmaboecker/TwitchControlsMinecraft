package net.stckoverflw.twitchcontrols.minecraft.twitch.impl

import com.github.twitch4j.pubsub.events.FollowingEvent
import net.axay.fabrik.core.item.itemStack
import net.axay.fabrik.core.text.literal
import net.axay.fabrik.igui.Gui
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Formatting
import net.stckoverflw.twitchcontrols.gui.selectActionGUI
import net.stckoverflw.twitchcontrols.minecraft.twitch.FollowEventData
import net.stckoverflw.twitchcontrols.minecraft.twitch.TwitchEvent
import java.util.*

const val followEventId = "follow"

object FollowEvent : TwitchEvent<FollowEventData, FollowingEvent>(followEventId, FollowingEvent::class.java) {

    override val icon: ItemStack = itemStack(Items.FLINT, 1) {
        setCustomName("Follow".literal.formatted(Formatting.LIGHT_PURPLE))
    }

    override fun gui(player: PlayerEntity): Gui = selectActionGUI(FollowEventData(UUID.randomUUID().toString()))

    override fun runEvent(
        event: FollowingEvent,
        eventData: FollowEventData,
        player: PlayerEntity
    ): Pair<Boolean, String> {
        return true to event.data.displayName
    }

}