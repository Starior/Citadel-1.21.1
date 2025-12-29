package com.github.alexthe666.citadel.item.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/**
 * Data component for citadel:fancy_item to display items with animations.
 * Replaces the old NBT-based system.
 */
public record FancyItemDisplay(
        String displayItem,
        boolean displayBob,
        boolean displayZoom,
        float displayScale,
        boolean displaySpin,
        boolean displayShake
) {
    public static final Codec<FancyItemDisplay> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("display_item").forGetter(FancyItemDisplay::displayItem),
            Codec.BOOL.optionalFieldOf("display_bob", false).forGetter(FancyItemDisplay::displayBob),
            Codec.BOOL.optionalFieldOf("display_zoom", false).forGetter(FancyItemDisplay::displayZoom),
            Codec.FLOAT.optionalFieldOf("display_scale", 1.0f).forGetter(FancyItemDisplay::displayScale),
            Codec.BOOL.optionalFieldOf("display_spin", false).forGetter(FancyItemDisplay::displaySpin),
            Codec.BOOL.optionalFieldOf("display_shake", false).forGetter(FancyItemDisplay::displayShake)
    ).apply(instance, FancyItemDisplay::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FancyItemDisplay> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public FancyItemDisplay decode(RegistryFriendlyByteBuf buf) {
            String displayItem = buf.readUtf();
            boolean displayBob = buf.readBoolean();
            boolean displayZoom = buf.readBoolean();
            float displayScale = buf.readFloat();
            boolean displaySpin = buf.readBoolean();
            boolean displayShake = buf.readBoolean();
            return new FancyItemDisplay(displayItem, displayBob, displayZoom, displayScale, displaySpin, displayShake);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, FancyItemDisplay value) {
            buf.writeUtf(value.displayItem());
            buf.writeBoolean(value.displayBob());
            buf.writeBoolean(value.displayZoom());
            buf.writeFloat(value.displayScale());
            buf.writeBoolean(value.displaySpin());
            buf.writeBoolean(value.displayShake());
        }
    };

    // Convenience constructor with defaults
    public FancyItemDisplay(String displayItem) {
        this(displayItem, false, false, 1.0f, false, false);
    }
}
