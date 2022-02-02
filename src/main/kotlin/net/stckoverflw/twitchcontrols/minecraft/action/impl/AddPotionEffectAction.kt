package net.stckoverflw.twitchcontrols.minecraft.action.impl

import net.axay.fabrik.core.item.itemStack
import net.axay.fabrik.core.item.setLore
import net.axay.fabrik.core.text.literal
import net.axay.fabrik.igui.*
import net.axay.fabrik.igui.observable.GuiProperty
import net.axay.fabrik.igui.observable.toGuiList
import net.minecraft.entity.effect.StatusEffectInstance
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
import net.stckoverflw.twitchcontrols.minecraft.action.AddPotionEffectData
import net.stckoverflw.twitchcontrols.minecraft.action.TwitchExecutorData
import net.stckoverflw.twitchcontrols.minecraft.addAction
import net.stckoverflw.twitchcontrols.minecraft.twitch.EventData

const val addPotionEffectId = "add-potion-effect"

class AddPotionEffectAction : Action<AddPotionEffectData>(addPotionEffectId) {
    override val icon: ItemStack = itemStack(Items.POTION, 1) {
        setCustomName("Add Effect".literal.formatted(Formatting.LIGHT_PURPLE))
    }

    override fun run(player: PlayerEntity, twitchData: TwitchExecutorData, data: AddPotionEffectData) {
        player.addStatusEffect(
            StatusEffectInstance(
                Registry.STATUS_EFFECT.get(data.effect),
                data.period * 20,
                data.level
            )
        )
    }

    override fun gui(eventData: EventData): Gui = igui(GuiType.NINE_BY_FIVE, "§9Add Effect".literal, 1) {
        val effectProperty = GuiProperty<Identifier?>(null)
        val levelProperty = GuiProperty(1)
        val durationProperty = GuiProperty(1)
        page(1, 1) {
            placeholder(Slots.All, grayPlaceholder)

            val compound = compound(
                (1 sl 2) rectTo (5 sl 8),
                Registry.STATUS_EFFECT.toGuiList(),
                iconGenerator = {
                    itemStack(Items.POTION, 1) {
                        setCustomName(it.name)
                    }
                },
                onClick = { event, element ->
                    effectProperty.set(Registry.STATUS_EFFECT.getId(element))
                    event.gui.changePage(event.gui.currentPage, event.gui.pagesByNumber[2]!!)
                }
            )

            compoundScrollForwards(1 sl 9, itemStack(Items.NETHERITE_BLOCK, 1) {
                setCustomName("Scroll Forward".literal.formatted(Formatting.GREEN))
            }.guiIcon, compound)
            compoundScrollBackwards(2 sl 9, itemStack(Items.NETHERITE_BLOCK, 1) {
                setCustomName("Scroll Backwards".literal.formatted(Formatting.RED))
            }.guiIcon, compound)
        }
        page(2, 2) {
            placeholder(Slots.All, grayPlaceholder)

            button(4 sl 4, GuiIcon.VariableIcon(levelProperty, levelProperty.guiIcon {
                itemStack(Items.AMETHYST_SHARD, 1) {
                    setCustomName(
                        "Level: ".literal.formatted(Formatting.AQUA)
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
                    levelProperty.set(levelProperty.get() + 1)
                } else if (it.type == GuiActionType.SHIFT_CLICK) {
                    if (levelProperty.get() > 1) {
                        levelProperty.set(levelProperty.get() - 1)
                    }
                }
            }

            button(4 sl 6, GuiIcon.VariableIcon(durationProperty, levelProperty.guiIcon {
                itemStack(Items.AMETHYST_SHARD, 1) {
                    setCustomName(
                        "Duration (seconds): ".literal.formatted(Formatting.AQUA)
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
                    durationProperty.set(durationProperty.get() + 1)
                } else if (it.type == GuiActionType.SHIFT_CLICK) {
                    if (durationProperty.get() > 1) {
                        durationProperty.set(durationProperty.get() - 1)
                    }
                }
            }

            button(2 sl 4, GuiIcon.VariableIcon(levelProperty, levelProperty.guiIcon {
                itemStack(Items.WRITABLE_BOOK, 1) {
                    setCustomName("Add Action".literal.formatted(Formatting.GREEN))
                    setLore(
                        listOf(
                            "Add level $it of the selected effect".literal.formatted(Formatting.GRAY)
                        )
                    )
                }
            }.iconGenerator)) {
                val effect = effectProperty.get()
                if (effect != null) {
                    EventManager.activeProfile[it.player.uuid]?.addAction(
                        eventData, AddPotionEffectData(
                            effect,
                            levelProperty.get(),
                            durationProperty.get()
                        )
                    )
                    it.player.openGui(settingsGUI(it.player), 1)
                }
            }

            button(2 sl 6, itemStack(Items.BARRIER, 1) {
                setCustomName("Go Back".literal.formatted(Formatting.RED))
                setLore(
                    listOf(
                        "Didn't select the right effect? go back to change it".literal.formatted(Formatting.GRAY)
                    )
                )
            }.guiIcon) {
                it.gui.changePage(it.gui.currentPage, it.gui.pagesByNumber[1]!!)
            }
        }
    }

    override fun getActionData(): AddPotionEffectData? = null
}