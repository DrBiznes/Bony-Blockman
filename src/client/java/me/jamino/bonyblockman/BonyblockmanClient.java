package me.jamino.bonyblockman;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BonyblockmanClient implements ClientModInitializer {
    private static final Map<UUID, SoundInstance> playingSounds = new HashMap<>();

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null) return;

            for (PlayerEntity player : client.world.getPlayers()) {
                UUID playerId = player.getUuid();

                if (BonyBlockmanItem.hasRecord(player.getMainHandStack())) {
                    if (!playingSounds.containsKey(playerId)) {
                        // Start playing the sound
                        SoundInstance soundInstance = new BlockmanMovingSound(
                                BonyBlockmanItem.getRecordSoundEvent(player.getMainHandStack()),
                                net.minecraft.sound.SoundCategory.RECORDS,
                                player,
                                ((IPlayerEntityMixin) player).getBlockmanLoopMode()
                        );
                        client.getSoundManager().play(soundInstance);
                        playingSounds.put(playerId, soundInstance);
                    } else {
                        // Update loop mode if it changed
                        SoundInstance soundInstance = playingSounds.get(playerId);
                        if (soundInstance instanceof BlockmanMovingSound) {
                            BlockmanMovingSound movingSound = (BlockmanMovingSound) soundInstance;
                            boolean shouldLoop = ((IPlayerEntityMixin) player).getBlockmanLoopMode();

                            // Instead of directly modifying `repeat`, create a method in BlockmanMovingSound to handle it
                            movingSound.setLooping(shouldLoop);
                        }
                    }
                } else {
                    // Stop the sound if it's playing
                    SoundInstance soundInstance = playingSounds.remove(playerId);
                    if (soundInstance != null) {
                        client.getSoundManager().stop(soundInstance);
                    }
                }
            }
        });

        // Clean up sounds when disconnecting
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            for (SoundInstance soundInstance : playingSounds.values()) {
                client.getSoundManager().stop(soundInstance);
            }
            playingSounds.clear();
        });
    }
}
