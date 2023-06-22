package com.tristankechlo.random_mob_sizes.mixin.geckolib;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tristankechlo.random_mob_sizes.mixin_access.MobMixinAddon;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


/* scale down the models for the geckolib models */
@Mixin(software.bernie.geckolib.renderer.GeoEntityRenderer.class)
public abstract class GeoEntityRendererMixin {

    @Inject(method = "preRender*", at = @At("TAIL"), remap = false)
    private <T extends Entity & software.bernie.geckolib.core.animatable.GeoAnimatable> void preRender$RandomMobSizes(
            PoseStack poseStack, T animatable, software.bernie.geckolib.cache.object.BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer,
            boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        if (animatable instanceof Mob) {
            float scale = ((MobMixinAddon) animatable).getMobScaling();
            poseStack.scale(scale, scale, scale);
        }
    }

}