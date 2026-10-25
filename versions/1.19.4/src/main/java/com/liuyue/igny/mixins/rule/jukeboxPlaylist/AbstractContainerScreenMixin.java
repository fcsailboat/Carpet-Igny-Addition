package com.liuyue.igny.mixins.rule.jukeboxPlaylist;

import com.liuyue.igny.helper.jukeboxPlaylist.JukeboxPlaylistHelper;
import com.liuyue.igny.utils.interfaces.jukeboxPlaylist.JukeboxPlaylistAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Screen.class)
public abstract class AbstractContainerScreenMixin {
    @Unique
    private BlockPos igny$containerPos;

    @Inject(method = "init()V", at = @At(value = "TAIL"))
    private void igny$captureContainerPos(CallbackInfo ci) {
        Screen self = (Screen) (Object) this;
        if (!(self instanceof AbstractContainerScreen<?>)) {
            return;
        }
        BlockPos pos = JukeboxPlaylistHelper.takeContainerPos();

        if (pos != null) {
            this.igny$containerPos = pos;
        }
    }

    @Inject(method = "getTooltipFromItem", at = @At(value = "RETURN"))
    private void igny$appendPlayingNext(ItemStack stack, CallbackInfoReturnable<List<Component>> cir) {
        Screen self = (Screen) (Object) this;
        if (!(self instanceof AbstractContainerScreen<?>)) {
            return;
        }
        Slot hoveredSlot = ((AbstractContainerScreenAccessor) this).igny$getHoveredSlot();
        Minecraft minecraft = Minecraft.getInstance();
        BlockPos pos = this.igny$containerPos;
        if (minecraft.level == null || minecraft.player == null || hoveredSlot == null || pos == null) {
            return;
        }
        if (!(minecraft.level.getBlockEntity(pos) instanceof Container target)
                || !(minecraft.level.getBlockEntity(pos.below()) instanceof JukeboxBlockEntity jukebox)) {
            return;
        }
        AbstractContainerMenu menu = minecraft.player.containerMenu;
        if (menu.slots.isEmpty()) {
            return;
        }
        Container container = menu.slots.getFirst().container;
        if (container.getContainerSize() < target.getContainerSize()) {
            return;
        }
        JukeboxPlaylistAccess access = (JukeboxPlaylistAccess) jukebox;
        int next = access.igny$getPlaylistNext();
        if (next < 0) {
            next = JukeboxPlaylistHelper.findNext(container, access.igny$getPlaylistPosition());
        }
        if (next >= 0 && hoveredSlot.index == next) {
            cir.getReturnValue().add(JukeboxPlaylistHelper.PLAYING_NEXT);
        }
    }
}
