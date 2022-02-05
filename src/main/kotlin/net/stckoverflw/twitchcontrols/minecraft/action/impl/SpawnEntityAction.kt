package net.stckoverflw.twitchcontrols.minecraft.action.impl

import net.axay.fabrik.core.item.itemStack
import net.axay.fabrik.core.item.setLore
import net.axay.fabrik.core.text.literal
import net.axay.fabrik.igui.*
import net.axay.fabrik.igui.observable.GuiProperty
import net.axay.fabrik.igui.observable.toGuiList
import net.minecraft.client.MinecraftClient
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.EntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.SpawnEggItem
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.stckoverflw.twitchcontrols.gui.item.grayPlaceholder
import net.stckoverflw.twitchcontrols.gui.settingsGUI
import net.stckoverflw.twitchcontrols.minecraft.EventManager
import net.stckoverflw.twitchcontrols.minecraft.action.Action
import net.stckoverflw.twitchcontrols.minecraft.action.SpawnEntityData
import net.stckoverflw.twitchcontrols.minecraft.action.TwitchExecutorData
import net.stckoverflw.twitchcontrols.minecraft.addAction
import net.stckoverflw.twitchcontrols.minecraft.twitch.EventData

const val spawnEntityId = "spawn-entity"

class SpawnEntityAction : Action<SpawnEntityData>(spawnEntityId) {

    override val icon = itemStack(Items.CREEPER_SPAWN_EGG) {
        setCustomName("Spawn a specific Entity".literal.formatted(Formatting.DARK_GREEN))
    }

    override fun run(player: PlayerEntity, twitchData: TwitchExecutorData, data: SpawnEntityData) {
        val worlds = MinecraftClient.getInstance().server?.worlds ?: return
        worlds.forEach {
            if (it == player.world) {
                val entity = Registry.ENTITY_TYPE.get(data.entity).create(player.world)
                it.spawnEntity(entity)
                entity?.teleport(player.x, player.y, player.z)
                if (data.includeTwitchName) {
                    entity?.customName = twitchData.executorName.literal
                    entity?.isCustomNameVisible = true
                }
            }
        }

    }

    override fun gui(eventData: EventData): Gui = igui(GuiType.NINE_BY_FIVE, "§cSpawn an Entity".literal, 1) {
        val entityProperty = GuiProperty<Identifier?>(null)
        val includeTwitchNameProperty = GuiProperty(false)
        page(1, 1) {
            placeholder(Slots.All, grayPlaceholder)

            val compound = compound(
                (1 sl 2) rectTo (5 sl 8),
                (Registry.ITEM.filterIsInstance<SpawnEggItem>().map {
                    EntityEntry(
                        Registry.ENTITY_TYPE.getId(it.getEntityType(it.defaultStack.nbt)),
                        it.defaultStack,
                        it.getEntityType(it.defaultStack.nbt).name
                    )
                } + listOf(
                    EntityEntry(
                        Registry.ENTITY_TYPE.getId(EntityType.WITHER),
                        Items.WITHER_SKELETON_SKULL.defaultStack,
                        "Wither".literal.formatted(Formatting.LIGHT_PURPLE),
                    ),
                    EntityEntry(
                        Registry.ENTITY_TYPE.getId(EntityType.TNT),
                        Items.TNT.defaultStack,
                        "TNT".literal.formatted(Formatting.RED),
                    )
                )).toGuiList(),
                iconGenerator = {
                    it.item.copy().apply {
                        setCustomName(it.name)
                        if (entityProperty.get() == it.identifier) {
                            addEnchantment(Enchantments.UNBREAKING, 1)
                            addHideFlag(ItemStack.TooltipSection.ENCHANTMENTS)
                        }
                    }
                },
                onClick = { event, element ->
                    entityProperty.set(element.identifier)
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

            button(4 sl 5, GuiIcon.VariableIcon(includeTwitchNameProperty, includeTwitchNameProperty.guiIcon {
                itemStack(Items.NAME_TAG, 1) {
                    setCustomName("Spawn with Twitch username".literal.formatted(if (it) Formatting.GREEN else Formatting.RED))
                    setLore(
                        listOf(
                            "The Entity will be spawned ".literal.formatted(Formatting.GRAY).append(
                                if (it) "with".literal.formatted(
                                    Formatting.GREEN
                                ) else "without".literal.formatted(Formatting.RED)
                            ),
                            "the name of the executing twitch user".literal.formatted(Formatting.GRAY),
                        )
                    )
                }
            }.iconGenerator)) {
                includeTwitchNameProperty.set(!includeTwitchNameProperty.get())
            }

            button(2 sl 4, GuiIcon.VariableIcon(entityProperty, entityProperty.guiIcon {
                itemStack(Items.WRITABLE_BOOK, 1) {
                    setCustomName("Add Action".literal.formatted(Formatting.GREEN))
                    setLore(
                        listOf(
                            "Spawn ${it?.path}".literal.formatted(Formatting.GRAY)
                        )
                    )
                }
            }.iconGenerator)) {
                val entity = entityProperty.get()
                if (entity != null) {
                    EventManager.activeProfile[it.player.uuid]?.addAction(
                        eventData, SpawnEntityData(
                            entity,
                            includeTwitchNameProperty.get()
                        )
                    )
                    it.player.openGui(settingsGUI(it.player), 1)
                }
            }

            button(2 sl 6, itemStack(Items.BARRIER, 1) {
                setCustomName("Go Back".literal.formatted(Formatting.RED))
                setLore(
                    listOf(
                        "Didn't select the right entity? go back to change it".literal.formatted(Formatting.GRAY)
                    )
                )
            }.guiIcon) {
                it.gui.changePage(it.gui.currentPage, it.gui.pagesByNumber[1]!!)
            }
        }
    }

    override fun getActionData(): SpawnEntityData? = null
}

private data class EntityEntry(
    val identifier: Identifier,
    val item: ItemStack,
    val name: Text
)
