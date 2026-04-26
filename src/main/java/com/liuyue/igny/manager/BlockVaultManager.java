package com.liuyue.igny.manager;

import com.google.gson.reflect.TypeToken;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlockVaultManager extends BaseDataManager<BlockVaultManager.VaultData> {
    public static final BlockVaultManager INSTANCE = new BlockVaultManager();

    private VaultData data;
    private boolean dirty = false;

    public static class VaultData {
        public Map<Long, String[]> vault = new HashMap<>();
        public Set<Long> pendingRestore = new HashSet<>();
    }

    @Override protected String getFileName() { return "amethyst_vault.json"; }
    @Override protected Type getDataType() { return new TypeToken<VaultData>(){}.getType(); }
    @Override public VaultData getDefaultData() { return new VaultData(); }

    @Override protected void applyData(VaultData data) {
        this.data = data != null ? data : new VaultData();
        this.dirty = false;
    }

    @Override public VaultData getCurrentData() {
        if (this.data == null) this.data = new VaultData();
        return this.data;
    }

    public void storeBlock(Level level, BlockPos pos, BlockState state) {
        String blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
        String snbt = "";

        BlockEntity be = level.getBlockEntity(pos);
        if (be != null) {
            HolderLookup.Provider provider = level.registryAccess();
            snbt = be.saveWithFullMetadata(provider).toString();
        }

        getCurrentData().vault.put(pos.asLong(), new String[]{blockId, snbt});
        this.dirty = true;
    }

    public boolean has(BlockPos pos) {
        return getCurrentData().vault.containsKey(pos.asLong());
    }

    public void restoreBlock(Level level, BlockPos pos) {
        String[] info = getCurrentData().vault.remove(pos.asLong());
        getCurrentData().pendingRestore.remove(pos.asLong());

        if (info == null || info.length < 1) return;

        this.dirty = true;
        ResourceLocation rl = ResourceLocation.tryParse(info[0]);
        if (rl == null) return;

        Block block = BuiltInRegistries.BLOCK.
                //#if MC >= 12102
                //$$ getValue(rl);
                //#else
                        get(rl);
        //#endif

        if (block != Blocks.AIR) {
            level.setBlock(pos, block.defaultBlockState(), 3);
            if (info.length > 1 && !info[1].isEmpty()) {
                try {
                    CompoundTag nbt = TagParser.parseTag(info[1]);
                    BlockEntity be = level.getBlockEntity(pos);
                    if (be != null) {
                        HolderLookup.Provider provider = level.registryAccess();
                        be.loadWithComponents(nbt, provider);
                        be.setChanged();
                    }
                } catch (Exception ignored) {}
            }
        }
    }

    public void markPending(BlockPos pos) {
        if (has(pos)) {
            if (getCurrentData().pendingRestore.add(pos.asLong())) this.dirty = true;
        }
    }
    public void scheduledSave() { if (this.dirty) { this.save(); this.dirty = false; } }
    public Set<Long> getPendingRestore() { return getCurrentData().pendingRestore; }
    @Override protected StorageScope getScope() { return StorageScope.WORLD; }
    @Override protected SideRestraint getSideRestraint() { return SideRestraint.COMMON; }
}