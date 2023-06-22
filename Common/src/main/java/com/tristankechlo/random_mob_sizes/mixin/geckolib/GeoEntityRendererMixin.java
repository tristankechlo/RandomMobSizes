package com.tristankechlo.random_mob_sizes.mixin.geckolib;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tristankechlo.random_mob_sizes.mixin_access.MobMixinAddon;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* scale down the models for the geckolib models */
@Mixin(software.bernie.geckolib3.renderers.geo.GeoEntityRenderer.class)
public abstract class GeoEntityRendererMixin {

    @Inject(method = "renderEarly*", at = @At("TAIL"), remap = false)
    private <T extends LivingEntity & software.bernie.geckolib3.core.IAnimatable> void renderEarly$RandomMobSizes(
            T animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource, VertexConsumer buffer,
            int packedLight, int packedOverlay, float red, float green, float blue, float partialTicks, CallbackInfo ci) {
        if (animatable instanceof Mob) {
            float scale = ((MobMixinAddon) animatable).getMobScaling();
            poseStack.scale(scale, scale, scale);
        }
    }

}
