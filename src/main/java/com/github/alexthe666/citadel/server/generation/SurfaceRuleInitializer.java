package com.github.alexthe666.citadel.server.generation;

import com.github.alexthe666.citadel.Citadel;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import java.util.Map;

/**
 * Initializes surface rules for all dimensions on server start.
 * This must be called AFTER biome sources are initialized to avoid breaking biome lookup.
 */
public class SurfaceRuleInitializer {

    /**
     * Initialize surface rules for all level stems on server start.
     * Called from ServerAboutToStartEvent handler.
     */
    public static void initializeOnServerStart(MinecraftServer server) {
        Citadel.LOGGER.info("[Citadel] SurfaceRuleInitializer: Starting initialization...");
        Citadel.LOGGER.info("[Citadel] OVERWORLD rules registered: {}", SurfaceRulesManager.hasRulesForCategory(SurfaceRulesManager.RuleCategory.OVERWORLD));
        
        RegistryAccess registryAccess = server.registryAccess();
        Registry<LevelStem> levelStemRegistry = registryAccess.registryOrThrow(Registries.LEVEL_STEM);

        for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : levelStemRegistry.entrySet()) {
            LevelStem stem = entry.getValue();
            Citadel.LOGGER.info("[Citadel] Processing dimension: {}", entry.getKey().location());
            initializeSurfaceRules(stem.type(), entry.getKey(), stem.generator());
        }
        Citadel.LOGGER.info("[Citadel] SurfaceRuleInitializer: Initialization complete.");
    }

    private static void initializeSurfaceRules(Holder<DimensionType> dimensionType, 
                                                ResourceKey<LevelStem> levelResourceKey, 
                                                ChunkGenerator chunkGenerator) {
        if (!(chunkGenerator instanceof NoiseBasedChunkGenerator noiseBasedChunkGenerator)) {
            return;
        }

        NoiseGeneratorSettings generatorSettings = noiseBasedChunkGenerator.generatorSettings().value();
        
        // Determine the rule category based on dimension
        SurfaceRulesManager.RuleCategory ruleCategory = getRuleCategoryForDimension(dimensionType);
        
        if (ruleCategory == null) {
            return;
        }

        // Only set the category if there are actually rules registered for it
        if (!SurfaceRulesManager.hasRulesForCategory(ruleCategory)) {
            return;
        }

        // Check if the mixin is applied before casting (cast through Object since compiler doesn't know about mixin)
        if (!((Object) generatorSettings instanceof IExtendedNoiseGeneratorSettings)) {
            Citadel.LOGGER.warn("NoiseGeneratorSettings mixin not applied, surface rules will not be injected for: {}", 
                levelResourceKey.location());
            return;
        }

        // Cast to our interface and set the rule category
        // This enables the mixin to start injecting surface rules
        ((IExtendedNoiseGeneratorSettings) (Object) generatorSettings).citadel$setRuleCategory(ruleCategory);
        
        Citadel.LOGGER.info("Initialized Citadel surface rules for dimension: {} (category: {})", 
            levelResourceKey.location(), ruleCategory);
    }

    /**
     * Determine the rule category based on dimension type.
     */
    private static SurfaceRulesManager.RuleCategory getRuleCategoryForDimension(Holder<DimensionType> dimensionType) {
        DimensionType type = dimensionType.value();
        
        // Check dimension characteristics to determine category
        // The Nether has ultraWarm=true, the End has no ceiling and different height
        if (type.ultraWarm()) {
            return SurfaceRulesManager.RuleCategory.NETHER;
        }
        
        // Check for End-like dimensions (no ceiling, specific height range)
        // End has minY=0, height=256, no ceiling
        if (!type.hasCeiling() && type.minY() == 0 && type.height() == 256 && !type.natural()) {
            return SurfaceRulesManager.RuleCategory.END;
        }
        
        // Default to overworld for natural dimensions
        if (type.natural()) {
            return SurfaceRulesManager.RuleCategory.OVERWORLD;
        }
        
        // For custom dimensions, try to make a reasonable guess
        // If it has a ceiling, it's probably nether-like
        if (type.hasCeiling()) {
            return SurfaceRulesManager.RuleCategory.NETHER;
        }
        
        // Default to overworld
        return SurfaceRulesManager.RuleCategory.OVERWORLD;
    }
}
