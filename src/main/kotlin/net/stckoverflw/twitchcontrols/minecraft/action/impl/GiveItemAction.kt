package net.stckoverflw.twitchcontrols.minecraft.action.impl

import net.axay.fabrik.core.item.itemStack
import net.axay.fabrik.core.item.setLore
import net.axay.fabrik.core.text.literal
import net.axay.fabrik.igui.*
import net.axay.fabrik.igui.observable.GuiProperty
import net.axay.fabrik.igui.observable.toGuiList
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.stckoverflw.twitchcontrols.gui.item.grayPlaceholder
import net.stckoverflw.twitchcontrols.gui.settingsGUI
import net.stckoverflw.twitchcontrols.minecraft.EventManager
import net.stckoverflw.twitchcontrols.minecraft.action.Action
import net.stckoverflw.twitchcontrols.minecraft.action.GiveItemData
import net.stckoverflw.twitchcontrols.minecraft.action.TwitchExecutorData
import net.stckoverflw.twitchcontrols.minecraft.addAction
import net.stckoverflw.twitchcontrols.minecraft.twitch.EventData
import net.stckoverflw.twitchcontrols.util.compoundScrolls
import net.stckoverflw.twitchcontrols.util.goBackButton

const val giveItemId = "give-item"

class GiveItemAction : Action<GiveItemData>(giveItemId) {
    override val icon: ItemStack = itemStack(Items.IRON_INGOT, 1) {
        setCustomName("Give Player an Item".literal.formatted(Formatting.YELLOW))
    }

    override fun run(player: PlayerEntity, twitchData: TwitchExecutorData, data: GiveItemData) {
        player.inventory.insertStack(Registry.ITEM.get(data.item).defaultStack.copy().apply {
            count = data.amount
        })
    }

    override fun gui(eventData: EventData): Gui = igui(GuiType.NINE_BY_FIVE, "§9Give Item".literal, 1) {
        val itemProperty = GuiProperty<Identifier?>(null)
        val amountProperty = GuiProperty(1)
        page(1, 1) {
            placeholder(Slots.All, grayPlaceholder)

            goBackButton()

            val compound = compound(
                (1 sl 2) rectTo (5 sl 8),
                Registry.ITEM.filter { it != Items.AIR }.toGuiList(),
                iconGenerator = {
                    it.defaultStack
                },
                onClick = { event, element ->
                    itemProperty.set(Registry.ITEM.getId(element))
                    event.gui.changePage(event.gui.currentPage, event.gui.pagesByNumber[2]!!)
                }
            )

            compoundScrolls(compound)
        }
        page(2, 2) {
            placeholder(Slots.All, grayPlaceholder)

            button(4 sl 5, GuiIcon.VariableIcon(amountProperty, amountProperty.guiIcon {
                itemStack(Items.AMETHYST_SHARD, 1) {
                    setCustomName(
                        "Amount: ".literal.formatted(Formatting.AQUA)
                            .append(it.toString().literal.formatted(Formatting.BLUE))
                    )
                    setLore(
                        listOf(
                            "".literal,
                            "Click to higher, shift click to lower".literal.formatted(Formatting.GRAY),
                            "".literal,
                        )
                    )
                }
            }.iconGenerator)) {
                if (it.type == GuiActionType.PICKUP) {
                    amountProperty.set(amountProperty.get() + 1)
                } else if (it.type == GuiActionType.SHIFT_CLICK) {
                    if (amountProperty.get() > 1) {
                        amountProperty.set(amountProperty.get() - 1)
                    }
                }
            }

            button(2 sl 4, GuiIcon.VariableIcon(amountProperty, amountProperty.guiIcon {
                itemStack(Items.WRITABLE_BOOK, 1) {
                    setCustomName("Add Action".literal.formatted(Formatting.GREEN))
                    setLore(
                        listOf(
                            "Add $it of the selected item to your inventory".literal.formatted(Formatting.GRAY)
                        )
                    )
                }
            }.iconGenerator)) {
                val item = itemProperty.get()
                if (item != null) {
                    EventManager.activeProfile[it.player.uuid]?.addAction(
                        eventData, GiveItemData(
                            item,
                            amountProperty.get()
                        )
                    )
                    it.player.openGui(settingsGUI(it.player), 1)
                }
            }

            button(2 sl 6, itemStack(Items.BARRIER, 1) {
                setCustomName("Go Back".literal.formatted(Formatting.RED))
                setLore(
                    listOf(
                        "Didn't select the right item? go back to change it".literal.formatted(Formatting.GRAY)
                    )
                )
            }.guiIcon) {
                it.gui.changePage(it.gui.currentPage, it.gui.pagesByNumber[1]!!)
            }
        }
    }

    override fun getActionData(): GiveItemData? = null
}