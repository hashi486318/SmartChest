package com.mokugyo.smartchest.registry;

import com.mokugyo.smartchest.SmartChest;
import com.mokugyo.smartchest.item.SmartChestBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, SmartChest.MOD_ID);

    public static final RegistryObject<BlockItem> SMART_CHEST_ITEM = ITEMS.register(
            "smart_chest",
            () -> new SmartChestBlockItem(ModBlocks.SMART_CHEST_BLOCK.get(), new Item.Properties())
    );
}
