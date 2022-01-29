package net.stckoverflw.twitchcontrols

import net.minecraft.util.Identifier

const val MOD_ID = "twitchcontrols"

val String.modId get() = Identifier(MOD_ID, this)