package com.github.alexthe666.citadel.server.generation;

/**
 * Interface for extending NoiseGeneratorSettings to support conditional surface rule injection.
 * This allows surface rules to only be applied during actual world generation, not during
 * biome source initialization which would break biome lookup.
 */
public interface IExtendedNoiseGeneratorSettings {
    void citadel$setRuleCategory(SurfaceRulesManager.RuleCategory ruleCategory);
    SurfaceRulesManager.RuleCategory citadel$getRuleCategory();
}
