package me.jamino.bonyblockman;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.registry.Registry; // Ensure the correct import for Registry
import net.minecraft.registry.Registries; // Ensure the correct import for Registries

public class BonyBlockmanItem extends Item {
    public static final String RECORD_KEY = "StoredRecord";
    public static final String LOOP_KEY = "LoopMode";
    public static final String SOUND_ID_KEY = "SoundId";

    public BonyBlockmanItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack blockmanStack = player.getStackInHand(hand);
        ItemStack offhandStack = player.getOffHandStack();

        if (!world.isClient) {
            NbtCompound nbt = blockmanStack.getOrCreateNbt();

            if (player.isSneaking()) {
                toggleLoopMode(blockmanStack, player);
                player.sendMessage(Text.literal("Loop mode: " + (isLoopMode(blockmanStack) ? "On" : "Off")), true);
            } else if (hasRecord(blockmanStack)) {
                ejectRecord(player, blockmanStack);
            } else if (offhandStack.getItem() instanceof MusicDiscItem) {
                insertRecord(player, blockmanStack, offhandStack);
            }
        }

        return TypedActionResult.success(blockmanStack);
    }

    private void ejectRecord(PlayerEntity player, ItemStack blockmanStack) {
        NbtCompound nbt = blockmanStack.getNbt();
        ItemStack recordStack = ItemStack.fromNbt(nbt.getCompound(RECORD_KEY));
        player.dropItem(recordStack, false);
        nbt.remove(RECORD_KEY);
        nbt.remove(LOOP_KEY);
        nbt.remove(SOUND_ID_KEY);

        // Use the method to get the world
        if (!player.getWorld().isClient) {
            ((IPlayerEntityMixin) player).setBlockmanLoopMode(false);
        }
    }

    private void insertRecord(PlayerEntity player, ItemStack blockmanStack, ItemStack recordStack) {
        NbtCompound nbt = blockmanStack.getOrCreateNbt();
        NbtCompound recordNbt = new NbtCompound();
        recordStack.writeNbt(recordNbt);
        nbt.put(RECORD_KEY, recordNbt);
        nbt.putBoolean(LOOP_KEY, false);

        MusicDiscItem musicDisc = (MusicDiscItem) recordStack.getItem();
        SoundEvent soundEvent = musicDisc.getSound();
        nbt.putString(SOUND_ID_KEY, soundEvent.getId().toString());

        player.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
        player.sendMessage(Text.literal("Now playing: " + musicDisc.getDescription().getString()), true);

        // Use the method to get the world
        if (!player.getWorld().isClient) {
            ((IPlayerEntityMixin) player).setBlockmanLoopMode(false);
        }
    }

    private void toggleLoopMode(ItemStack stack, PlayerEntity player) {
        NbtCompound nbt = stack.getOrCreateNbt();
        boolean currentMode = nbt.getBoolean(LOOP_KEY);
        nbt.putBoolean(LOOP_KEY, !currentMode);

        // Use the method to get the world
        if (!player.getWorld().isClient) {
            ((IPlayerEntityMixin) player).setBlockmanLoopMode(!currentMode);
        }
    }

    public static boolean hasRecord(ItemStack stack) {
        return stack.getOrCreateNbt().contains(RECORD_KEY);
    }

    public static boolean isLoopMode(ItemStack stack) {
        return stack.getOrCreateNbt().getBoolean(LOOP_KEY);
    }

    public static SoundEvent getRecordSoundEvent(ItemStack stack) {
        String soundIdString = stack.getOrCreateNbt().getString(SOUND_ID_KEY);
        Identifier soundId = new Identifier(soundIdString);
        // Use Registries class properly to get the SoundEvent
        return Registries.SOUND_EVENT.get(soundId);
    }
}
