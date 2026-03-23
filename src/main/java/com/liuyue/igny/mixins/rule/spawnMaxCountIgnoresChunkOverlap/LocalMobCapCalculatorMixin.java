package com.liuyue.igny.mixins.rule.spawnMaxCountIgnoresChunkOverlap;

import com.liuyue.igny.IGNYSettings;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LocalMobCapCalculator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = LocalMobCapCalculator.class, priority = 1100)
public class LocalMobCapCalculatorMixin {
    @Unique
    private static MinecraftServer server;

    @Shadow
    private List<ServerPlayer> getPlayersNear(ChunkPos pos) {
        return null;
    }

    @Inject(method = "canSpawn", at = @At("HEAD"))
    private void canSpawn(MobCategory category, ChunkPos pos, CallbackInfoReturnable<Boolean> cir) {
        List<ServerPlayer> serverPlayers = this.getPlayersNear(pos);
        if (serverPlayers != null && !serverPlayers.isEmpty()) {
            ServerPlayer player = serverPlayers.getFirst();
            server = player.level().getServer();
        }
    }

    @Mixin(targets = "net.minecraft.world.level.LocalMobCapCalculator$MobCounts")
    static class MobCountsMixin {
        @Shadow
        @Final
        private Object2IntMap<MobCategory> counts;

        @Inject(method = "canSpawn", at = @At(value = "HEAD"), cancellable = true)
        private void canSpawn(MobCategory category, CallbackInfoReturnable<Boolean> cir) {
            if (IGNYSettings.spawnMaxCountIgnoresChunkOverlap && LocalMobCapCalculatorMixin.server != null) {
                cir.setReturnValue(this.counts.getOrDefault(category, 0) < category.getMaxInstancesPerChunk() * LocalMobCapCalculatorMixin.server.getPlayerCount());
            }
        }
    }
}
