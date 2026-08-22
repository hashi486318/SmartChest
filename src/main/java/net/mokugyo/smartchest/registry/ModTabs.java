package net.mokugyo.smartchest.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static net.mokugyo.smartchest.SmartChest.MOD_ID;
import static net.mokugyo.smartchest.registry.ModItems.SMART_CHEST_ITEM;

public class ModTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SMART_CHEST_TAB =
            CREATIVE_MODE_TABS.register("smart_chest_tab",
                    () -> CreativeModeTab.builder()
                            .icon(() -> SMART_CHEST_ITEM.get().getDefaultInstance())
                            .title(Component.translatable("creativetab.smartchest.tab"))
                            .displayItems((parameters, output) -> output.accept(SMART_CHEST_ITEM.get()))
                            .build());
}
