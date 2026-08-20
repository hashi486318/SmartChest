package com.mokugyo.cokemod.init;

import com.mokugyo.cokemod.Cokemod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

public final class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Cokemod.MOD_ID);

    public static final Supplier<CreativeModeTab> COKE_TAB = TABS.register("coke_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.cokemod.coke_tab"))
            .icon(() -> new ItemStack(ModItems.COKE.get()))
            .displayItems((parameters, output) -> {
                output.accept(ModItems.COKE.get());
                output.accept(ModItems.COKE_MASS.get());
            })
            .build());

    private ModCreativeTab() {}
}
