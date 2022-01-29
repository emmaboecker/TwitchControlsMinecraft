package net.stckoverflw.twitchcontrols.minecraft

import kotlinx.serialization.Serializable
import net.stckoverflw.twitchcontrols.minecraft.action.ActionData
import net.stckoverflw.twitchcontrols.minecraft.twitch.EventData
import java.util.*

@Serializable
data class Profile(
    val name: String,
    val player: String,
    val actions: Map<EventData, ActionData>
)

fun Profile.addAction(eventData: EventData, actionData: ActionData): Profile {
    val newProfile = copy(
        actions = actions + (eventData to actionData)
    )
    updateProfile(newProfile)
    return newProfile
}

fun Profile.removeAction(eventData: EventData): Profile {
    val newProfile = copy(
        actions = actions - eventData
    )
    updateProfile(newProfile)
    return newProfile
}

private fun Profile.updateProfile(newProfile: Profile) {
    val uuid = UUID.fromString(player)
    if (this.name == EventManager.activeProfile[uuid]?.name) {
        EventManager.activeProfile[uuid] = newProfile
    }

    EventManager.profiles -= this
    EventManager.profiles += newProfile
    EventManager.saveProfiles()
}
