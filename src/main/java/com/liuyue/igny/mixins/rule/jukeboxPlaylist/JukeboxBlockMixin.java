package com.liuyue.igny.mixins.rule.jukeboxPlaylist;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.helper.jukeboxPlaylist.JukeboxPlaylistHelper;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
//? <= 1.20.6 ? import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(JukeboxBlock.class)
public abstract class JukeboxBlockMixin {
    //#if >= 1.21.1
    @Inject(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/JukeboxBlockEntity;popOutTheItem()V", shift = At.Shift.AFTER))
    private void igny$advanceOnClick(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult,
                                     CallbackInfoReturnable<InteractionResult> cir, @Local JukeboxBlockEntity jukebox) {
        if (IGNYSettings.JUKEBOX_PLAYLIST.value()) {
            JukeboxPlaylistHelper.advance(level, pos, jukebox);
        }
    }
    //#else
    /*$$@Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/JukeboxBlockEntity;popOutTheItem()V", shift = At.Shift.AFTER))
    private void igny$advanceOnClick(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                     BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir, @Local JukeboxBlockEntity jukebox) {
        if (IGNYSettings.JUKEBOX_PLAYLIST.value()) {
            JukeboxPlaylistHelper.advance(level, pos, jukebox);
        }
    }$$*/
    //#endif
}
