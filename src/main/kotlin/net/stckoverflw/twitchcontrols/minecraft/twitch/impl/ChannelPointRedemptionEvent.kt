package net.stckoverflw.twitchcontrols.minecraft.twitch.impl

import com.github.twitch4j.pubsub.events.RewardRedeemedEvent
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
import net.stckoverflw.twitchcontrols.minecraft.twitch.ChannelPointRedemptionEventData
import net.stckoverflw.twitchcontrols.minecraft.twitch.TwitchEvent
import net.stckoverflw.twitchcontrols.twitchEventsClients
import net.stckoverflw.twitchcontrols.util.goBackButton
import net.stckoverflw.twitchcontrols.util.twitchChannel
import net.stckoverflw.twitchcontrols.util.twitchToken

const val channelPointEventId = "channel-points"

object ChannelPointRedemptionEvent : TwitchEvent<ChannelPointRedemptionEventData, RewardRedeemedEvent>(
    channelPointEventId,
    RewardRedeemedEvent::class.java
) {

    override val icon: ItemStack = itemStack(Items.GLOW_ITEM_FRAME, 1) {
        setCustomName("Channel Point Redemption".literal.formatted(Formatting.GOLD))
    }

    override fun gui(player: PlayerEntity) = igui(GuiType.NINE_BY_FIVE, "§9Channel Point Redemptions".literal, 1) {
        page(1, 1) {
            placeholder(Slots.All, grayPlaceholder)

            goBackButton()

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

    override fun runEvent(
        event: RewardRedeemedEvent,
        eventData: ChannelPointRedemptionEventData,
        player: PlayerEntity
    ): Pair<Boolean, String> {
        return (event.redemption.reward.title == eventData.title) to event.redemption.user.displayName
    }
}