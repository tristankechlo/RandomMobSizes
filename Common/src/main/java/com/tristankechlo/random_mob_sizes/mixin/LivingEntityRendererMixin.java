package com.tristankechlo.random_mob_sizes.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tristankechlo.random_mob_sizes.mixin_access.MobMixinAddon;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* scale down the models for all mobs, according to their saved scaling factor */
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;scale(Lnet/minecraft/world/entity/LivingEntity;Lcom/mojang/blaze3d/vertex/PoseStack;F)V",
                    shift = At.Shift.AFTER))
    private void afterScale$RandomMobSizes(LivingEntity entity, float p_115309_, float p_115310_, PoseStack poseStack, MultiBufferSource p_115312_, int p_115313_, CallbackInfo ci) {
        // called after the normal scale method, to make sure the scaling is applied to all mobs
        // only scale Mob entities and not Players, ArmorStands, etc.
        if (entity instanceof Mob) {
            float scale = ((MobMixinAddon) entity).getMobScaling();
            poseStack.scale(scale, scale, scale);
        }
    }

}
