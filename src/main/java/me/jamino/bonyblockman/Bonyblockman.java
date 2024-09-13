package me.jamino.bonyblockman;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class Bonyblockman implements ModInitializer {
    public static final String MOD_ID = "bonyblockman";
    public static final Item BONY_BLOCKMAN = new BonyBlockmanItem(new Item.Settings().maxCount(1));

    @Override
    public void onInitialize() {
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "bony_blockman"), BONY_BLOCKMAN);
    }
}
