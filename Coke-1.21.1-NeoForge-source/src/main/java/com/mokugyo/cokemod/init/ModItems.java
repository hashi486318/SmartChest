package com.mokugyo.cokemod.init;

import com.mokugyo.cokemod.Cokemod;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Cokemod.MOD_ID);

    public static final Supplier<Item> COKE = ITEMS.registerItem("coke", Item::new);
    public static final Supplier<Item> COKE_MASS = ITEMS.registerItem("coke_mass", Item::new);

    private ModItems() {}
}
