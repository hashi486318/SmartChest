package com.mokugyo.smartchest;

import com.mokugyo.smartchest.network.ModPackets;
import com.mokugyo.smartchest.registry.ModBlockEntities;
import com.mokugyo.smartchest.registry.ModMenus;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static com.mokugyo.smartchest.registry.ModBlocks.BLOCKS;
import static com.mokugyo.smartchest.registry.ModItems.ITEMS;
import static com.mokugyo.smartchest.registry.ModTabs.CREATIVE_MODE_TABS;

@Mod(SmartChest.MOD_ID)
public class SmartChest {
    public static final String MOD_ID = "smartchest";

    public SmartChest() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        ModBlockEntities.BLOCKENTITIES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ModMenus.SMART_CHEST_MENUS.register(modEventBus);
        ModPackets.register();
    }
}