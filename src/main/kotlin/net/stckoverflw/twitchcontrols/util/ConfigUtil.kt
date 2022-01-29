package net.stckoverflw.twitchcontrols.util

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.entity.player.PlayerEntity
import net.stckoverflw.twitchcontrols.MOD_ID
import java.nio.file.Path
import kotlin.io.path.*

private val configPath: Path
    get() {
        val path = FabricLoader.getInstance().configDir.resolve("$MOD_ID/")
        path.createDirectories()
        return path
    }

@Serializable
private data class TwitchData(
    val token: String?,
    val channel: String?
)

var PlayerEntity.twitchToken: String?
    get() {
        val configFile = configPath.resolve("$uuidAsString.json")
        return JSON.decodeFromString<TwitchData>(configFile.readText()).token
    }
    set(value) {
        val configFile = configPath.resolve("$uuidAsString.json")
        if (!configFile.exists()) {
            configFile.createFile()
            configFile.writeText(
                JSON.encodeToString(
                    TwitchData(
                        token = value,
                        channel = null
                    )
                )
            )
        } else {
            configFile.writeText(
                JSON.encodeToString(
                    JSON.decodeFromString<TwitchData>(configFile.readText()).copy(
                        token = value
                    )
                )
            )
        }
    }

var PlayerEntity.twitchChannel: String?
    get() {
        val configFile = configPath.resolve("$uuidAsString.json")
        return JSON.decodeFromString<TwitchData>(configFile.readText()).channel
    }
    set(value) {
        val configFile = configPath.resolve("$uuidAsString.json")
        if (!configFile.exists()) {
            configFile.createFile()
            configFile.writeText(
                JSON.encodeToString(
                    TwitchData(
                        token = null,
                        channel = value
                    )
                )
            )
        } else {
            configFile.writeText(
                JSON.encodeToString(
                    JSON.decodeFromString<TwitchData>(configFile.readText()).copy(
                        channel = value
                    )
                )
            )
        }
    }