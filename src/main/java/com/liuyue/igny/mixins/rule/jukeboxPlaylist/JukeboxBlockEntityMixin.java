package com.liuyue.igny.mixins.rule.jukeboxPlaylist;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.helper.jukeboxPlaylist.JukeboxPlaylistHelper;
import com.liuyue.igny.utils.interfaces.jukeboxPlaylist.JukeboxPlaylistAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JukeboxBlockEntity.class)
public abstract class JukeboxBlockEntityMixin extends BlockEntity implements JukeboxPlaylistAccess {
    @Unique
    private int igny$playlistPosition = -1;

    @Unique
    private int igny$playlistNext = -1;

    public JukeboxBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    //#if >= 1.21.1
    @Inject(method = "onSongChanged", at = @At(value = "TAIL"))
    private void igny$onSongEnded(CallbackInfo ci) {
        JukeboxBlockEntity self = (JukeboxBlockEntity) (Object) this;
        Level level = self.getLevel();
        if (level == null || level.isClientSide() || !IGNYSettings.JUKEBOX_PLAYLIST.value()) {
            return;
        }
        if (self.getSongPlayer().isPlaying()) {
            return;
        }
        if (!JukeboxPlaylistHelper.playable(self.getTheItem())) {
            return;
        }
        JukeboxPlaylistHelper.advance(level, self.getBlockPos(), self);
    }
    //#else
    /*$$@Inject(method = "playRecordTick", at = @At(value = "RETURN"))
    private static void igny$jukeboxPlaylistTick(Level level, BlockPos pos, BlockState state, JukeboxBlockEntity jukebox, CallbackInfo ci) {
        if (level.isClientSide() || !IGNYSettings.JUKEBOX_PLAYLIST.value()) {
            return;
        }
        if (!jukebox.isRecordPlaying()) {
            JukeboxPlaylistHelper.advance(level, pos, jukebox);
        }
    }$$*/
    //#endif

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if (level == null || level.isClientSide() || !IGNYSettings.JUKEBOX_PLAYLIST.value()) {
            return;
        }
        JukeboxBlockEntity self = (JukeboxBlockEntity) (Object) this;
        if (self.getSongPlayer().isPlaying()) { //#replace < 1.21.1 ? if (self.isRecordPlaying()) {
            return;
        }
        if (!self.getTheItem().isEmpty()) { //#replace < 1.20.4 ? if (!self.getItem(0).isEmpty()) {
            return;
        }
        JukeboxPlaylistHelper.advance(level, self.getBlockPos(), self);
    }

    //#if >= 1.21.8
    /*$$@Inject(method = "saveAdditional", at = @At(value = "RETURN"))
    private void igny$savePlaylistPosition(net.minecraft.world.level.storage.ValueOutput output, CallbackInfo ci) {
        if (this.igny$playlistPosition >= 0) {
            output.putInt("playlist_position", this.igny$playlistPosition);
        }
    }

    @Inject(method = "loadAdditional", at = @At(value = "RETURN"))
    private void igny$loadPlaylistPosition(net.minecraft.world.level.storage.ValueInput input, CallbackInfo ci) {
        input.getInt("playlist_position").ifPresent(value -> this.igny$playlistPosition = value);
    }$$*/
    //#elseif >= 1.21.5
    /*$$@Inject(method = "saveAdditional", at = @At(value = "RETURN"))
    private void igny$savePlaylistPosition(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries, CallbackInfo ci) {
        if (this.igny$playlistPosition >= 0) {
            tag.putInt("playlist_position", this.igny$playlistPosition);
            tag.putInt("playlist_next", this.igny$playlistNext);
        }
    }

    @Inject(method = "loadAdditional", at = @At(value = "RETURN"))
    private void igny$loadPlaylistPosition(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries, CallbackInfo ci) {
        tag.getInt("playlist_position").ifPresent(value -> this.igny$playlistPosition = value);
    }$$*/
    //#else
    @Inject(method = "saveAdditional", at = @At(value = "RETURN"))
    private void igny$savePlaylistPosition(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries, CallbackInfo ci) { //#replace < 1.20.6 ? private void igny$savePlaylistPosition(CompoundTag tag, CallbackInfo ci) {
        if (this.igny$playlistPosition >= 0) {
            tag.putInt("playlist_position", this.igny$playlistPosition);
            tag.putInt("playlist_next", this.igny$playlistNext);
        }
    }

    @Inject(method = "loadAdditional", at = @At(value = "RETURN")) //#replace < 1.20.6 ? @Inject(method = "load", at = @At(value = "RETURN"))
    private void igny$loadPlaylistPosition(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries, CallbackInfo ci) { //#replace < 1.20.6 ? private void igny$loadPlaylistPosition(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("playlist_position")) {
            this.igny$playlistPosition = tag.getInt("playlist_position");
        }
        if (tag.contains("playlist_next")) {
            this.igny$playlistNext = tag.getInt("playlist_next");
        }
    }
    //#endif

    @Override
    public int igny$getPlaylistPosition() {
        return this.igny$playlistPosition;
    }

    @Override
    public void igny$setPlaylistPosition(int position) {
        this.igny$playlistPosition = position;
    }

    @Override
    public int igny$getPlaylistNext() {
        return this.igny$playlistNext;
    }

    @Override
    public void igny$setPlaylistNext(int slot) {
        this.igny$playlistNext = slot;
    }

    //#if >= 26.1
    /*$$@Override
    public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt("playlist_position", this.igny$playlistPosition);
        tag.putInt("playlist_next", this.igny$playlistNext);
        return tag;
    }$$*/
    //#elseif >= 1.21.1
    @Override
    public @NotNull CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider registries) {
        CompoundTag tag = this.saveWithoutMetadata(registries);
        tag.putInt("playlist_position", this.igny$playlistPosition);
        tag.putInt("playlist_next", this.igny$playlistNext);
        return tag;
    }
    //#elseif >= 1.20.6
    /*$$@Override
    public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider registries) {
        CompoundTag tag = ((JukeboxBlockEntity) (Object) this).saveWithoutMetadata(registries);
        tag.putInt("playlist_position", this.igny$playlistPosition);
        tag.putInt("playlist_next", this.igny$playlistNext);
        return tag;
    }$$*/
    //#else
    /*$$@Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = ((JukeboxBlockEntity) (Object) this).saveWithoutMetadata();
        tag.putInt("playlist_position", this.igny$playlistPosition);
        tag.putInt("playlist_next", this.igny$playlistNext);
        return tag;
    }$$*/
    //#endif
}
