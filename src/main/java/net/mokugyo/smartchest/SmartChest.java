package net.mokugyo.smartchest;

import net.mokugyo.smartchest.network.ModPackets;
import net.mokugyo.smartchest.registry.ModBlockEntities;
import net.mokugyo.smartchest.registry.ModMenus;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import static net.mokugyo.smartchest.registry.ModBlocks.BLOCKS;
import static net.mokugyo.smartchest.registry.ModItems.ITEMS;
import static net.mokugyo.smartchest.registry.ModTabs.CREATIVE_MODE_TABS;

@Mod(SmartChest.MOD_ID)
public class SmartChest {
    public static final String MOD_ID = "smartchest";

    public SmartChest(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        ModBlockEntities.BLOCKENTITIES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ModMenus.SMART_CHEST_MENUS.register(modEventBus);
        modEventBus.addListener(ModPackets::register);
    }
}