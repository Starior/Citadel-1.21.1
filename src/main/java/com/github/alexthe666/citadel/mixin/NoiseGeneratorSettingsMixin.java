package com.github.alexthe666.citadel.mixin;

import com.github.alexthe666.citadel.server.generation.IExtendedNoiseGeneratorSettings;
import com.github.alexthe666.citadel.server.generation.SurfaceRulesManager;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to inject custom surface rules from SurfaceRulesManager into world generation.
 * Uses a conditional approach similar to TerraBlender - rules are only injected when
 * the ruleCategory has been set, which happens during server initialization AFTER
 * biome sources are fully initialized. This prevents breaking biome lookup.
 */
@Mixin(value = NoiseGeneratorSettings.class, priority = 500)
public class NoiseGeneratorSettingsMixin implements IExtendedNoiseGeneratorSettings {

    @Shadow
    private SurfaceRules.RuleSource surfaceRule;

    @Unique
    private SurfaceRulesManager.RuleCategory citadel$ruleCategory = null;

    @Unique
    private SurfaceRules.RuleSource citadel$mergedSurfaceRule = null;

    @Inject(method = "surfaceRule", at = @At("HEAD"), cancellable = true)
    private void citadel$surfaceRule(CallbackInfoReturnable<SurfaceRules.RuleSource> cir) {
        // Only inject rules if the category has been set (after biome source init)
        if (this.citadel$ruleCategory != null) {
            // Cache the merged rules to avoid recomputing every call
            if (this.citadel$mergedSurfaceRule == null) {
                this.citadel$mergedSurfaceRule = SurfaceRulesManager.mergeRulesForCategory(
                    this.citadel$ruleCategory, this.surfaceRule);
            }
            cir.setReturnValue(this.citadel$mergedSurfaceRule);
        }
    }

    @Override
    public void citadel$setRuleCategory(SurfaceRulesManager.RuleCategory ruleCategory) {
        this.citadel$ruleCategory = ruleCategory;
        // Reset cached rules when category changes
        this.citadel$mergedSurfaceRule = null;
    }

    @Override
    public SurfaceRulesManager.RuleCategory citadel$getRuleCategory() {
        return this.citadel$ruleCategory;
    }
}
