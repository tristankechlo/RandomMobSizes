package com.tristankechlo.random_mob_sizes.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tristankechlo.random_mob_sizes.mixin_access.MobMixinAddon;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/* scale down the shadow of all mobs according to their saved scaling factor */
@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;FFLnet/minecraft/world/level/LevelReader;F)V"))
    private void redirectRenderShadow(PoseStack poseStack, MultiBufferSource buffer, Entity entity, float p_114459_, float p_114460_, LevelReader level, float shadowRadius) {
        if (entity instanceof Mob) {
            shadowRadius *= ((MobMixinAddon) entity).getMobScaling();
        }
        renderShadow(poseStack, buffer, entity, p_114459_, p_114460_, level, shadowRadius);
    }

    @Shadow
    private static void renderShadow(PoseStack poseStack, MultiBufferSource buffer, Entity entity, float p_114459_, float p_114460_, LevelReader level, float shadowRadius) {
        throw new AssertionError("Mixin failed to shadow method");
    }

}
