package com.liuyue.igny.mixins.rule.jukeboxPlaylist;

import com.liuyue.igny.helper.jukeboxPlaylist.JukeboxPlaylistHelper;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
    @Inject(method = "useItemOn", at = @At(value = "RETURN"))
    private void igny$offerContainerPos(LocalPlayer player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        InteractionResult result = cir.getReturnValue();

        if (result != null && result.consumesAction()) {
            JukeboxPlaylistHelper.offerContainerPos(hitResult.getBlockPos());
        }
    }
}
