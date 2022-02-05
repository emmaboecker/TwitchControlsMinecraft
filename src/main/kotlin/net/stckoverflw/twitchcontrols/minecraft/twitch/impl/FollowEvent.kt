package net.stckoverflw.twitchcontrols.minecraft.twitch.impl

import com.github.twitch4j.pubsub.events.FollowingEvent
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import net.axay.fabrik.core.item.itemStack
import net.axay.fabrik.core.text.literal
import net.axay.fabrik.igui.Gui
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Formatting
import net.stckoverflw.twitchcontrols.gui.selectActionGUI
import net.stckoverflw.twitchcontrols.minecraft.EventManager
import net.stckoverflw.twitchcontrols.minecraft.action.TwitchExecutorData
import net.stckoverflw.twitchcontrols.minecraft.twitch.FollowEventData
import net.stckoverflw.twitchcontrols.minecraft.twitch.TwitchEvent
import net.stckoverflw.twitchcontrols.util.JSON
import net.stckoverflw.twitchcontrols.util.playEventSound
import java.util.*
import com.github.philippheuer.events4j.core.EventManager as TwitchEventManager

const val followEventId = "follow"

object FollowEvent : TwitchEvent<FollowEventData>(followEventId) {

    override val icon: ItemStack = itemStack(Items.FLINT, 1) {
        setCustomName("Follow".literal.formatted(Formatting.LIGHT_PURPLE))
    }

    override fun gui(player: PlayerEntity): Gui = selectActionGUI(FollowEventData(UUID.randomUUID().toString()))

    override fun runEvent(eventManager: TwitchEventManager, player: PlayerEntity) {
        eventManager.onEvent(FollowingEvent::class.java) {
            player.playEventSound()
            val activeProfile = EventManager.activeProfile[player.uuid] ?: return@onEvent
            activeProfile.actions.forEach { (_, actionData) ->
                EventManager.actions.forEach { currentAction ->
                    if (currentAction.actionId == JSON.encodeToJsonElement(actionData).jsonObject["action"]
                            .toString().replace("\"", "")
                    ) {
                        currentAction.runSafe(
                            player,
                            TwitchExecutorData(it.data.displayName),
                            actionData
                        )
                    }
                }
            }
        }
    }

}