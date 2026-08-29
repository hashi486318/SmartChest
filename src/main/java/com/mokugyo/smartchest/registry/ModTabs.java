package com.mokugyo.smartchest.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.mokugyo.smartchest.SmartChest.MOD_ID;
import static com.mokugyo.smartchest.registry.ModItems.SMART_CHEST_ITEM;

public class ModTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final RegistryObject<CreativeModeTab> SMART_CHEST_TAB =
            CREATIVE_MODE_TABS.register("smart_chest_tab",
                    () -> CreativeModeTab.builder()
                            .icon(() -> SMART_CHEST_ITEM.get().getDefaultInstance())
                            .title(Component.translatable("creativetab.smartchest.tab"))
                            .displayItems((parameters, output) -> output.accept(SMART_CHEST_ITEM.get()))
                            .build());
}
