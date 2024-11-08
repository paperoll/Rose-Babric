package me.ht9.rose.mixin.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import me.ht9.rose.Rose;
import me.ht9.rose.event.events.PacketEvent;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = NetworkManager.class)
public class MixinNetworkManager
{
    @Inject(
            method = "addToSendQueue",
            at = @At(
                    "HEAD"
            ),
            cancellable = true
    )
    private void send(Packet packet, CallbackInfo ci)
    {
        PacketEvent event = new PacketEvent(packet, true);
        Rose.bus().post(event);
        if (event.cancelled())
        {
            ci.cancel();
        }
    }

    @Inject(
            method = "readPacket",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;add(Ljava/lang/Object;)Z"
            ),
            cancellable = true
    )
    public void receive(CallbackInfoReturnable<Boolean> cir, @Local Packet packet)
    {
        PacketEvent event = new PacketEvent(packet, false);
        Rose.bus().post(event);
        if (event.cancelled())
        {
            cir.cancel();
        }
    }
}