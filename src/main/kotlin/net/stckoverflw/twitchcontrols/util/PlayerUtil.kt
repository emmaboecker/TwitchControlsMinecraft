package net.stckoverflw.twitchcontrols.util

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents

fun PlayerEntity.playEventSound() {
    if (!world.isClient) {
        world.playSound(
            null,
            blockPos,
            SoundEvents.BLOCK_NOTE_BLOCK_PLING,
            SoundCategory.BLOCKS,
            1f,
            1f
        )
    }
}