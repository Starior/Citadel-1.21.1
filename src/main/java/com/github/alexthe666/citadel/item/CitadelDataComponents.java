package com.github.alexthe666.citadel.item;

import com.github.alexthe666.citadel.item.data.FancyItemDisplay;
import com.github.alexthe666.citadel.item.data.IconItemDisplay;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public class CitadelDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = 
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "citadel");

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FancyItemDisplay>> FANCY_ITEM_DISPLAY = 
            DATA_COMPONENTS.register("fancy_item_display", () -> DataComponentType.<FancyItemDisplay>builder()
                    .persistent(FancyItemDisplay.CODEC)
                    .networkSynchronized(FancyItemDisplay.STREAM_CODEC)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<IconItemDisplay>> ICON_ITEM_DISPLAY = 
            DATA_COMPONENTS.register("icon_item_display", () -> DataComponentType.<IconItemDisplay>builder()
                    .persistent(IconItemDisplay.CODEC)
                    .networkSynchronized(IconItemDisplay.STREAM_CODEC)
                    .build());
}
