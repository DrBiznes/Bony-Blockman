package me.jamino.bonyblockman;

import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random; // Import the Random class

public class BlockmanMovingSound extends MovingSoundInstance {
    private final PlayerEntity player;

    public BlockmanMovingSound(SoundEvent soundEvent, SoundCategory category, PlayerEntity player, boolean looping) {
        super(soundEvent, category, Random.create()); // Provide a Random instance
        this.player = player;
        this.repeat = looping;
        this.repeatDelay = 0;
        this.x = (float) player.getX();
        this.y = (float) player.getY();
        this.z = (float) player.getZ();
    }

    @Override
    public void tick() {
        if (player.isRemoved() || !BonyBlockmanItem.hasRecord(player.getMainHandStack())) {
            this.setDone();
            return;
        }
        this.x = (float) player.getX();
        this.y = (float) player.getY();
        this.z = (float) player.getZ();
    }

    /**
     * Sets the looping mode of the sound.
     *
     * @param shouldLoop Whether the sound should loop.
     */
    public void setLooping(boolean shouldLoop) {
        this.repeat = shouldLoop; // Since repeat is protected, we can access it here
    }
}
