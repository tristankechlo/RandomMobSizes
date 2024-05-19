package com.tristankechlo.random_mob_sizes.mixin.entity;

import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({AbstractPiglin.class, AbstractSkeleton.class, AbstractVillager.class, CaveSpider.class, Cow.class, Dolphin.class,
        EnderMan.class, Endermite.class, Fox.class, Ghast.class, Shulker.class, Silverfish.class, SnowGolem.class,
        Spider.class, Witch.class, WitherSkeleton.class, Zombie.class, ZombifiedPiglin.class})
public abstract class EyeHeightMixin {

    /*@Inject(method = "getStandingEyeHeight", at = @At("RETURN"), cancellable = true)
    private void getStandingEyeHeight$RandomMobSizes(Pose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
        if (MixinHelper.EYE_HEIGHTS.containsKey(this.getClass())) {
            float modifier = MixinHelper.EYE_HEIGHTS.get(this.getClass());
            cir.setReturnValue(dimensions.height() * modifier);
        }
    }*/

}