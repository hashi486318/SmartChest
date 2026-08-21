package net.mokugyo.smartchest;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.mokugyo.smartchest.network.ModPackets;
import net.mokugyo.smartchest.registry.ModBlockEntities;
import net.mokugyo.smartchest.registry.ModBlocks;
import net.mokugyo.smartchest.registry.ModMenus;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.mokugyo.smartchest.block.SmartChestBlock;

import static net.mokugyo.smartchest.registry.ModBlocks.BLOCKS;
import static net.mokugyo.smartchest.registry.ModItems.ITEMS;
import static net.mokugyo.smartchest.registry.ModTabs.CREATIVE_MODE_TABS;

@Mod(SmartChest.MOD_ID)
public class SmartChest {
    public static final String MOD_ID = "smartchest";

    // コンストラクタでイベントバスに登録
    public SmartChest(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        ModBlockEntities.BLOCKENTITIES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ModMenus.SMART_CHEST_MENUS.register(modEventBus);
        modEventBus.addListener(ModPackets::register);
    }
}