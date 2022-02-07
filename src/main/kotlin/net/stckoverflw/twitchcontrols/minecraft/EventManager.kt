package net.stckoverflw.twitchcontrols.minecraft

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.entity.player.PlayerEntity
import net.stckoverflw.twitchcontrols.MOD_ID
import net.stckoverflw.twitchcontrols.minecraft.action.impl.*
import net.stckoverflw.twitchcontrols.minecraft.twitch.impl.*
import net.stckoverflw.twitchcontrols.util.JSON
import java.util.*
import kotlin.io.path.*

object EventManager {

    private val profilePath = FabricLoader.getInstance().configDir.resolve("$MOD_ID/profiles/")

    val actions = listOf(
        SpawnEntityAction(),
        LitPlayerOnFireAction(),
        GiveItemAction(),
        AddPotionEffectAction(),
        PlaceLavaAction(),
        TpUpAction(),
        RandomTeleportAction(),
        ClearInventoryAction(),
        KillAction()
    )

    val twitchEvents = listOf(
        ChannelPointRedemptionEvent,
        FollowEvent,
        SubscribeEvent,
        SubGiftSingularEvent,
        SubGiftMultipleEvent,
        BitSingularEvent,
        BitMultipleEvent,
    )

    var profiles = emptyList<Profile>()
    var activeProfile = hashMapOf<UUID, Profile?>()

    operator fun invoke() {
        profiles = loadProfiles()
    }

    fun getProfilesForPlayer(player: PlayerEntity) = profiles.filter { it.player == player.uuidAsString }

    fun loadProfiles(): List<Profile> {
        profilePath.createDirectories()
        return profilePath.listDirectoryEntries("*.profile.json").map {
            JSON.decodeFromString(it.readText())
        }
    }

    fun saveProfiles() {
        profilePath.createDirectories()
        profiles.forEach {
            val propertiesFileName =
                it.player + "-" + it.name.lowercase().replace("\\W".toRegex(), "") + ".profile.json"
            val profileFile = profilePath / propertiesFileName

            if (!profileFile.exists()) {
                profileFile.createFile()
            }

            profileFile.writeText(JSON.encodeToString(it))
        }
    }
}