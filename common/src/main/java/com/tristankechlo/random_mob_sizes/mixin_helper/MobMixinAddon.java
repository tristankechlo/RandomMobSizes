package com.tristankechlo.random_mob_sizes.mixin_helper;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.level.ServerLevelAccessor;

public interface MobMixinAddon {

    boolean shouldScaleLoot$RandomMobSizes();

    void setShouldScaleLoot$RandomMobSizes(boolean shouldScale);

    boolean shouldScaleXP$RandomMobSizes();

    void setShouldScaleXP$RandomMobSizes(boolean shouldScale);

    // called by CaveSpiderMixin to apply scaling to CaveSpiders
    void doFinalizeSpawn$RandomMobSizes(ServerLevelAccessor level);

    float addModifier$RandomMobSizes(Holder<Attribute> attribute, float scaling, boolean ceil);

}
