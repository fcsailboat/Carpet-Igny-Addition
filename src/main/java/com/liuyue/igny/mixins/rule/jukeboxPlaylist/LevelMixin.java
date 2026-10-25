package com.liuyue.igny.mixins.rule.jukeboxPlaylist;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.helper.jukeboxPlaylist.JukeboxPlaylistHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level.class)
public abstract class LevelMixin {
    @Inject(method = "blockEntityChanged", at = @At(value = "HEAD"))
    private void igny$onContainerChanged(BlockPos pos, CallbackInfo ci) {
        Level level = (Level) (Object) this;
        if (level.isClientSide() || !IGNYSettings.JUKEBOX_PLAYLIST.value()) {
            return;
        }
        BlockPos jukeboxPos = pos.below();
        if (!(level.getBlockEntity(jukeboxPos) instanceof JukeboxBlockEntity jukebox)) {
            return;
        }
        if (jukebox.getSongPlayer().isPlaying()) { //#replace < 1.21.1 ? if (jukebox.isRecordPlaying()) {
            JukeboxPlaylistHelper.refreshNext(level, jukeboxPos, jukebox);
            return;
        }
        if (!jukebox.getTheItem().isEmpty()) { //#replace < 1.20.4 ? if (!jukebox.getItem(0).isEmpty()) {
            return;
        }
        JukeboxPlaylistHelper.advance(level, jukeboxPos, jukebox);
    }
}
