package net.mokugyo.smartchest.registry;

import net.mokugyo.smartchest.SmartChest;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(SmartChest.MOD_ID);

    public static final DeferredItem<BlockItem> SMART_CHEST_ITEM = ITEMS.register(
            "smart_chest",
            () -> new BlockItem(ModBlocks.SMART_CHEST_BLOCK.get(), new Item.Properties())
    );
}