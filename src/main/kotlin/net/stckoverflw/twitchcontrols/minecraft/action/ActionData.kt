package net.stckoverflw.twitchcontrols.minecraft.action

import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import net.minecraft.util.Identifier
import net.stckoverflw.twitchcontrols.minecraft.action.impl.addPotionEffectId
import net.stckoverflw.twitchcontrols.minecraft.action.impl.giveItemId
import net.stckoverflw.twitchcontrols.minecraft.action.impl.litPlayerOnFireId
import net.stckoverflw.twitchcontrols.minecraft.action.impl.spawnEntityId

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("action")
sealed class ActionData

@Serializable
@SerialName(spawnEntityId)
data class SpawnEntityData(
    @Contextual val entity: Identifier,
    val includeTwitchName: Boolean
) : ActionData() {
    override fun toString(): String = "spawn entity ${entity.path} with name of user $includeTwitchName"
}

@Serializable
@SerialName(litPlayerOnFireId)
data class LitPlayerOnFireData(
    val periodInSeconds: Int
) : ActionData()

@Serializable
@SerialName(giveItemId)
data class GiveItemData(
    @Contextual val item: Identifier,
    val amount: Int
) : ActionData() {
    override fun toString(): String = "give $amount ${item.path}"
}

@Serializable
@SerialName(addPotionEffectId)
data class AddPotionEffectData(
    @Contextual val effect: Identifier,
    val level: Int,
    val period: Int,
) : ActionData() {
    override fun toString(): String = "add effect ${effect.path} level $level for $period seconds"
}
