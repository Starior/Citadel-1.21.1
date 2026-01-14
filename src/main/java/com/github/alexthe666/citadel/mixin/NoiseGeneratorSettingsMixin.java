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
 * Always merges overworld rules unconditionally.
 */
@Mixin(value = NoiseGeneratorSettings.class, priority = 500)
public class NoiseGeneratorSettingsMixin implements IExtendedNoiseGeneratorSettings {

    @Shadow
    private SurfaceRules.RuleSource surfaceRule;

    @Unique
    private SurfaceRulesManager.RuleCategory citadel$ruleCategory = null;

    @Unique
    private SurfaceRules.RuleSource citadel$mergedSurfaceRule = null;

    @Unique
    private boolean citadel$initialized = false;

    @Inject(method = "surfaceRule", at = @At("HEAD"), cancellable = true)
    private void citadel$surfaceRule(CallbackInfoReturnable<SurfaceRules.RuleSource> cir) {
        // Always merge overworld rules unconditionally
        if (!this.citadel$initialized) {
            this.citadel$mergedSurfaceRule = SurfaceRulesManager.mergeOverworldRules(this.surfaceRule);
            this.citadel$initialized = true;
        }
        if (this.citadel$mergedSurfaceRule != null) {
            cir.setReturnValue(this.citadel$mergedSurfaceRule);
        }
    }

    @Override
    public void citadel$setRuleCategory(SurfaceRulesManager.RuleCategory ruleCategory) {
        this.citadel$ruleCategory = ruleCategory;
        this.citadel$mergedSurfaceRule = null;
        this.citadel$initialized = false;
    }

    @Override
    public SurfaceRulesManager.RuleCategory citadel$getRuleCategory() {
        return this.citadel$ruleCategory;
    }
}
