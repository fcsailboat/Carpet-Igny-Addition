package com.liuyue.igny.helper.jukeboxPlaylist;

import com.liuyue.igny.utils.interfaces.jukeboxPlaylist.JukeboxPlaylistAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import org.jetbrains.annotations.Nullable;

public final class JukeboxPlaylistHelper {
    public static final Component PLAYING_NEXT = Component.translatable("igny.rule.record.playingNext");

    private static BlockPos pendingContainerPos;

    private static final ThreadLocal<Boolean> ADVANCING = ThreadLocal.withInitial(() -> false);

    private JukeboxPlaylistHelper() {
    }

    public static void offerContainerPos(BlockPos pos) {
        pendingContainerPos = pos;
    }

    @Nullable
    public static BlockPos takeContainerPos() {
        BlockPos pos = pendingContainerPos;
        pendingContainerPos = null;
        return pos;
    }

    public static Container containerAbove(Level level, BlockPos pos) {
        return level.getBlockEntity(pos.above()) instanceof Container container ? container : null;
    }

    public static boolean playable(ItemStack stack) {
        return !stack.isEmpty() && stack.has(net.minecraft.core.component.DataComponents.JUKEBOX_PLAYABLE); //#replace < 1.21.1 ? return !stack.isEmpty() && stack.getItem() instanceof net.minecraft.world.item.RecordItem;
    }

    public static int findNext(Container container, int fromSlot) {
        int size = container.getContainerSize();
        for (int offset = 1; offset <= size; offset++) {
            int slot = (fromSlot + offset) % size;
            if (slot < 0) {
                slot += size;
            }
            if (playable(container.getItem(slot))) {
                return slot;
            }
        }
        return -1;
    }

    public static void advance(Level level, BlockPos pos, JukeboxBlockEntity jukebox) {
        if (ADVANCING.get()) {
            return;
        }
        Container container = containerAbove(level, pos);
        if (container == null) {
            return;
        }
        JukeboxPlaylistAccess access = (JukeboxPlaylistAccess) jukebox;
        int currentSlot = access.igny$getPlaylistPosition();
        int nextSlot = findNext(container, currentSlot);

        if (nextSlot < 0) {
            access.igny$setPlaylistNext(-1);
            return;
        }

        ADVANCING.set(true);
        try {
            ItemStack taken = container.getItem(nextSlot);
            container.setItem(nextSlot, jukebox.getTheItem()); //#replace < 1.20.4 ? container.setItem(nextSlot, jukebox.getItem(0));
            jukebox.setTheItem(taken); //#replace < 1.20.4 ? jukebox.setItem(0, taken);
            access.igny$setPlaylistPosition(nextSlot);
            access.igny$setPlaylistNext(findNext(container, nextSlot));
            broadcast(jukebox);
        } finally {
            ADVANCING.set(false);
        }
    }

    public static void refreshNext(Level level, BlockPos pos, JukeboxBlockEntity jukebox) {
        Container container = containerAbove(level, pos);
        if (container == null) {
            return;
        }
        JukeboxPlaylistAccess access = (JukeboxPlaylistAccess) jukebox;
        access.igny$setPlaylistNext(findNext(container, access.igny$getPlaylistPosition()));
        broadcast(jukebox);
    }

    private static void broadcast(JukeboxBlockEntity jukebox) {
        if (jukebox.getLevel() instanceof ServerLevel serverLevel) {
            serverLevel.getServer().getPlayerList().broadcastAll(ClientboundBlockEntityDataPacket.create(jukebox));
        }
    }
}
