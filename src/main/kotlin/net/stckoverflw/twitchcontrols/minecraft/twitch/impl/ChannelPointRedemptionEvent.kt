package net.stckoverflw.twitchcontrols.minecraft.twitch.impl

import com.github.twitch4j.pubsub.events.RewardRedeemedEvent
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import net.axay.fabrik.core.item.itemStack
import net.axay.fabrik.core.item.setLore
import net.axay.fabrik.core.text.literal
import net.axay.fabrik.igui.*
import net.axay.fabrik.igui.observable.toGuiList
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Formatting
import net.stckoverflw.twitchcontrols.gui.item.grayPlaceholder
import net.stckoverflw.twitchcontrols.gui.selectActionGUI
import net.stckoverflw.twitchcontrols.minecraft.EventManager
import net.stckoverflw.twitchcontrols.minecraft.action.TwitchExecutorData
import net.stckoverflw.twitchcontrols.minecraft.twitch.ChannelPointRedemptionEventData
import net.stckoverflw.twitchcontrols.minecraft.twitch.TwitchEvent
import net.stckoverflw.twitchcontrols.twitchEventsClients
import net.stckoverflw.twitchcontrols.util.JSON
import net.stckoverflw.twitchcontrols.util.playEventSound
import net.stckoverflw.twitchcontrols.util.twitchChannel
import net.stckoverflw.twitchcontrols.util.twitchToken
import com.github.philippheuer.events4j.core.EventManager as TwitchEventManager

const val channelPointEventId = "channel-points"

object ChannelPointRedemptionEvent : TwitchEvent<ChannelPointRedemptionEventData>(channelPointEventId) {

    override val icon: ItemStack = itemStack(Items.GLOW_ITEM_FRAME, 1) {
        setCustomName("Channel Point Redemption".literal.formatted(Formatting.GOLD))
    }

    override fun gui(player: PlayerEntity) = igui(GuiType.NINE_BY_FIVE, "§9Channel Point Redemptions".literal, 1) {
        page(1, 1) {
            placeholder(Slots.All, grayPlaceholder)

            try {
                val redemptions = twitchEventsClients[player.uuid]!!.twitch4jClient.helix
                    .getCustomRewards(
                        player.twitchToken,
                        player.twitchChannel,
                        null,
                        null
                    ).execute().rewards
                compound(
                    (2 sl 2) rectTo (4 sl 8),
                    redemptions.toGuiList(),
                    iconGenerator = {
                        itemStack(Items.GLOW_ITEM_FRAME, 1) {
                            setCustomName(it.title.literal)
                        }
                    },
                    onClick = { event, element ->
                        event.player.openGui(selectActionGUI(ChannelPointRedemptionEventData(element.title)), 1)
                    }
                )
            } catch (_: Exception) {
                placeholder(3 sl 2, itemStack(Items.BARRIER, 1) {
                    setCustomName("§cCould not load Redemptions".literal)
                    setLore(
                        listOf(
                            "§7Please set a correct twitch token using".literal,
                            "§9/twitch connect token §7to use this feature".literal
                        )
                    )
                }.guiIcon)
            }

        }
    }

    override fun runEvent(eventManager: TwitchEventManager, player: PlayerEntity) {
        eventManager.onEvent(RewardRedeemedEvent::class.java) {
            player.playEventSound()
            val activeProfile = EventManager.activeProfile[player.uuid] ?: return@onEvent
            activeProfile.actions.forEach { (eventType, actionData) ->
                if (eventType == ChannelPointRedemptionEventData(title = it.redemption.reward.title)) {
                    EventManager.actions.forEach { currentAction ->
                        if (currentAction.actionId == JSON.encodeToJsonElement(actionData).jsonObject["action"]
                                .toString().replace("\"", "")
                        ) {
                            currentAction.runSafe(
                                player,
                                TwitchExecutorData(it.redemption.user.displayName),
                                actionData
                            )
                        }
                    }
                }
            }
        }
    }
}