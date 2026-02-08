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
 * Uses the rule category assigned by SurfaceRuleInitializer to avoid cross-dimension leakage.
 * Merged order: original rules first, then Citadel rules (append), so other mods (WorldWeaver, Blueprint, etc.) take priority.
 */
@Mixin(value = NoiseGeneratorSettings.class, priority = 500)
public class NoiseGeneratorSettingsMixin implements IExtendedNoiseGeneratorSettings {

    @Shadow
    private SurfaceRules.RuleSource surfaceRule;

    @Unique
    private SurfaceRulesManager.RuleCategory citadel$ruleCategory = null;

    @Inject(method = "surfaceRule", at = @At("HEAD"), cancellable = true)
    private void citadel$surfaceRule(CallbackInfoReturnable<SurfaceRules.RuleSource> cir) {
        // Never cache: always use current field so other mods (WorldWeaver, Blueprint) that
        // overwrite surfaceRule later are included in the merge (original first, then Citadel).
        if (this.citadel$ruleCategory != null && SurfaceRulesManager.hasRulesForCategory(this.citadel$ruleCategory)) {
            cir.setReturnValue(SurfaceRulesManager.mergeRulesForCategory(this.citadel$ruleCategory, this.surfaceRule));
        }
    }

    @Override
    public void citadel$setRuleCategory(SurfaceRulesManager.RuleCategory ruleCategory) {
        this.citadel$ruleCategory = ruleCategory;
    }

    @Override
    public SurfaceRulesManager.RuleCategory citadel$getRuleCategory() {
        return this.citadel$ruleCategory;
    }
}
