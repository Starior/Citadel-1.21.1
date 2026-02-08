package com.github.alexthe666.citadel.server.generation;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.ArrayList;
import java.util.List;

public class SurfaceRulesManager {
    private static final List<SurfaceRules.RuleSource> OVERWORLD_REGISTRY = new ArrayList<>();
    private static final List<SurfaceRules.RuleSource> NETHER_REGISTRY = new ArrayList<>();
    private static final List<SurfaceRules.RuleSource> END_REGISTRY = new ArrayList<>();
    private static final List<SurfaceRules.RuleSource> CAVE_REGISTRY = new ArrayList<>();

    /**
     * Categories for surface rules to be classed under.
     * Used to conditionally apply rules only during actual world generation.
     */
    public enum RuleCategory {
        OVERWORLD, NETHER, END
    }

    public SurfaceRulesManager() {
    }

    public static void registerOverworldSurfaceRule(SurfaceRules.ConditionSource condition, SurfaceRules.RuleSource rule) {
        registerOverworldSurfaceRule(SurfaceRules.ifTrue(condition, rule));
    }

    public static void registerOverworldSurfaceRule(SurfaceRules.RuleSource rule) {
        OVERWORLD_REGISTRY.add(rule);
    }

    public static void registerNetherSurfaceRule(SurfaceRules.ConditionSource condition, SurfaceRules.RuleSource rule) {
        registerNetherSurfaceRule(SurfaceRules.ifTrue(condition, rule));
    }

    public static void registerNetherSurfaceRule(SurfaceRules.RuleSource rule) {
        NETHER_REGISTRY.add(rule);
    }

    public static void registerEndSurfaceRule(SurfaceRules.ConditionSource condition, SurfaceRules.RuleSource rule) {
        registerEndSurfaceRule(SurfaceRules.ifTrue(condition, rule));
    }

    public static void registerEndSurfaceRule(SurfaceRules.RuleSource rule) {
        END_REGISTRY.add(rule);
    }

    public static void registerCaveSurfaceRule(SurfaceRules.ConditionSource condition, SurfaceRules.RuleSource rule) {
        registerCaveSurfaceRule(SurfaceRules.ifTrue(condition, rule));
    }

    public static void registerCaveSurfaceRule(SurfaceRules.RuleSource rule) {
        CAVE_REGISTRY.add(rule);
    }

    /**
     * Merges Citadel surface rules with the existing (vanilla/other mods) rules.
     * Original rules are placed FIRST so that WorldWeaver, Blueprint, and vanilla
     * get priority; Citadel rules are appended and only apply where no prior rule matched
     * (e.g. Alex's Caves biomes). This avoids Citadel overwriting other mods' surface rules.
     */
    public static SurfaceRules.RuleSource mergeRules(SurfaceRules.RuleSource prev, List<SurfaceRules.RuleSource> toMerge) {
        ImmutableList.Builder<SurfaceRules.RuleSource> builder = ImmutableList.builder();
        builder.add(prev);
        builder.addAll(toMerge);
        return SurfaceRules.sequence(builder.build().toArray(SurfaceRules.RuleSource[]::new));
    }

    public static SurfaceRules.RuleSource mergeOverworldRules(SurfaceRules.RuleSource rulesIn) {
        return mergeRules(rulesIn, OVERWORLD_REGISTRY);
    }

    public static SurfaceRules.RuleSource mergeNetherRules(SurfaceRules.RuleSource rulesIn) {
        return mergeRules(rulesIn, NETHER_REGISTRY);
    }

    public static SurfaceRules.RuleSource mergeEndRules(SurfaceRules.RuleSource rulesIn) {
        return mergeRules(rulesIn, END_REGISTRY);
    }

    /**
     * Merge rules based on the given category.
     * @param category The rule category (dimension type)
     * @param rulesIn The original surface rules (vanilla/other mods)
     * @return Merged surface rules with original first, then Citadel rules appended
     */
    public static SurfaceRules.RuleSource mergeRulesForCategory(RuleCategory category, SurfaceRules.RuleSource rulesIn) {
        return switch (category) {
            case OVERWORLD -> mergeOverworldRules(rulesIn);
            case NETHER -> mergeNetherRules(rulesIn);
            case END -> mergeEndRules(rulesIn);
        };
    }

    /**
     * Check if there are any registered rules for the given category.
     */
    public static boolean hasRulesForCategory(RuleCategory category) {
        return switch (category) {
            case OVERWORLD -> !OVERWORLD_REGISTRY.isEmpty();
            case NETHER -> !NETHER_REGISTRY.isEmpty();
            case END -> !END_REGISTRY.isEmpty();
        };
    }
}