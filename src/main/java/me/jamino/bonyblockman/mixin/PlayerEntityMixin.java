package me.jamino.bonyblockman.mixin;

import me.jamino.bonyblockman.IPlayerEntityMixin;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements IPlayerEntityMixin {
    @Unique
    private static final TrackedData<Boolean> BLOCKMAN_LOOP_MODE = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void initDataTracker(CallbackInfo ci) {
        ((PlayerEntity) (Object) this).getDataTracker().startTracking(BLOCKMAN_LOOP_MODE, false);
    }

    @Override
    public void setBlockmanLoopMode(boolean loopMode) {
        ((PlayerEntity) (Object) this).getDataTracker().set(BLOCKMAN_LOOP_MODE, loopMode);
    }

    @Override
    public boolean getBlockmanLoopMode() {
        return ((PlayerEntity) (Object) this).getDataTracker().get(BLOCKMAN_LOOP_MODE);
    }
}
