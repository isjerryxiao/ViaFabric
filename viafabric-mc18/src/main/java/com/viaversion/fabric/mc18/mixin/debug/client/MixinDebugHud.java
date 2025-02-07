package com.viaversion.fabric.mc18.mixin.debug.client;

import com.viaversion.fabric.common.handler.CommonTransformer;
import com.viaversion.fabric.common.handler.FabricDecodeHandler;
import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.connection.UserConnection;
import io.netty.channel.ChannelHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

import java.util.List;

@Mixin(DebugHud.class)
public class MixinDebugHud {
    @Inject(at = @At("RETURN"), method = "getLeftText")
    protected void getLeftText(CallbackInfoReturnable<List<String>> info) {
        String line = "[ViaFabric] I: " + Via.getManager().getConnectionManager().getConnections().size() + " (F: "
                + Via.getManager().getConnectionManager().getConnectedClients().size() + ")";
        ChannelHandler viaDecoder = ((MixinClientConnectionAccessor) MinecraftClient.getInstance().getNetworkHandler()
                .getClientConnection()).getChannel().pipeline().get(CommonTransformer.HANDLER_DECODER_NAME);
        if (viaDecoder instanceof FabricDecodeHandler) {
            UserConnection user = ((FabricDecodeHandler) viaDecoder).getInfo();
            ProtocolInfo protocol = user.getProtocolInfo();
            if (protocol != null) {
                ProtocolVersion serverVer = ProtocolVersion.getProtocol(protocol.getServerProtocolVersion());
                ProtocolVersion clientVer = ProtocolVersion.getProtocol(protocol.getProtocolVersion());
                line += " / C: " + clientVer + " S: " + serverVer + " A: " + user.isActive();
            }
        }

        info.getReturnValue().add(line);
    }
}
