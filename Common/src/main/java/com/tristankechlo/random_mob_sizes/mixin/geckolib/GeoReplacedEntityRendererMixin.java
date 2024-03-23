package com.tristankechlo.random_mob_sizes.mixin.geckolib;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tristankechlo.random_mob_sizes.mixin_helper.MobMixinAddon;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(software.bernie.geckolib3.renderers.geo.GeoReplacedEntityRenderer.class)
public abstract class GeoReplacedEntityRendererMixin {

    @Inject(method = "render*", at = @At(value = "INVOKE",
            target = "Lsoftware/bernie/geckolib3/renderers/geo/GeoReplacedEntityRenderer;preRenderCallback(Lnet/minecraft/world/entity/LivingEntity;Lcom/mojang/blaze3d/vertex/PoseStack;F)V",
            shift = At.Shift.AFTER))
    private void preRenderCallback$RandomMobSizes(Entity entity, software.bernie.geckolib3.core.IAnimatable animatable,
                                                  float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        if (entity instanceof Mob) {
            float scale = ((MobMixinAddon) entity).getMobScaling$RandomMobSizes();
            poseStack.scale(scale, scale, scale);
        }
    }

}
