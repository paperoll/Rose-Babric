package me.ht9.rose.mixin.mixins;

import me.ht9.rose.Rose;
import me.ht9.rose.event.events.RenderEntityEvent;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderLiving.class)
public abstract class MixinRenderLiving extends Render
{
    @Shadow protected ModelBase mainModel;
    @Unique private float lastPartialTicks;

    @Inject(
            method = "doRender(Lnet/minecraft/src/EntityLiving;DDDFF)V",
            at = @At(
                    value = "HEAD"
            )
    )
    public void doRender(EntityLiving entityLiving, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci)
    {
        this.lastPartialTicks = partialTicks;
    }

    @Redirect(
            method = "doRender(Lnet/minecraft/src/EntityLiving;DDDFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/ModelBase;render(FFFFFF)V"
            )
    )
    public void doRender$BeforeRenderModel(ModelBase instance, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityLiving entityLiving)
    {
        RenderEntityEvent.Type modelType = instance.equals(mainModel) ? RenderEntityEvent.Type.MAIN : RenderEntityEvent.Type.RENDERPASS;

        RenderEntityEvent event = new RenderEntityEvent(
                instance,
                entityLiving,
                limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale,
                RenderEntityEvent.Stage.PRE,
                modelType,
                this.lastPartialTicks
        );

        Rose.bus().post(event);

        instance.render(
                event.limbSwing(),
                event.limbSwingAmount(),
                event.ageInTicks(),
                event.netHeadYaw(),
                event.headPitch(),
                event.scale()
        );

        RenderEntityEvent postEvent = new RenderEntityEvent(
                instance,
                entityLiving,
                limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale,
                RenderEntityEvent.Stage.POST,
                modelType,
                this.lastPartialTicks
        );

        Rose.bus().post(postEvent);
    }
}