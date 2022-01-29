package net.stckoverflw.twitchcontrols.minecraft.action

import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import net.minecraft.util.Identifier

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("action")
sealed class ActionData

@Serializable
@SerialName("spawn-entity")
data class SpawnEntityData(
    @Contextual val entity: Identifier,
    val includeTwitchName: Boolean
) : ActionData() {
    override fun toString(): String = "spawn entity ${entity.path} with name of user $includeTwitchName"
}

@Serializable
@SerialName("lit-player-on-fire")
data class LitPlayerOnFireData(
    val periodInSeconds: Int
) : ActionData()
