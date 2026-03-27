package com.liuyue.igny.mixins.carpet.fix.fakePlayerMemoryLeakFix;

//#if MC < 26.1
import carpet.patches.NetHandlerPlayServerFake;
import com.liuyue.igny.IGNYSettings;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
//#if MC >= 12002
import net.minecraft.server.network.CommonListenerCookie;
//#endif
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#else
//$$ import com.liuyue.igny.utils.compat.DummyClass;
//#endif
import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 26.1
//$$ @Mixin(DummyClass.class)
//#else
@Mixin(NetHandlerPlayServerFake.class)
//#endif
public class NetHandlerPlayServerFakeMixin extends ServerGamePacketListenerImpl {
    //#if MC >= 12002
    public NetHandlerPlayServerFakeMixin(MinecraftServer server, Connection connection, ServerPlayer player, CommonListenerCookie cookie)
    //#else
    //$$ public NetHandlerPlayServerFakeMixin(MinecraftServer server, Connection connection, ServerPlayer player)
    //#endif
    {
        //#if MC >= 12002
        super(server, connection, player, cookie);
        //#else
        //$$ super(server, connection, player);
        //#endif
    }

    //#if MC < 26.1
    @Inject(method = "send", at = @At(value = "HEAD"))
    private void send(Packet<?> packetIn, CallbackInfo ci) {
        if (IGNYSettings.fakePlayerMemoryLeakFix) {
            super.send(packetIn);
        }
    }
    //#endif
}
