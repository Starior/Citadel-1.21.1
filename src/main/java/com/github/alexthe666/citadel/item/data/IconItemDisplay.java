package com.github.alexthe666.citadel.item.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Data component for citadel:icon_item to display custom icons.
 * Replaces the old NBT-based system.
 */
public record IconItemDisplay(String iconLocation) {
    public static final Codec<IconItemDisplay> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("icon_location").forGetter(IconItemDisplay::iconLocation)
    ).apply(instance, IconItemDisplay::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, IconItemDisplay> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, IconItemDisplay::iconLocation,
            IconItemDisplay::new
    );
}
