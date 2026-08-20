package com.mokugyo.cokemod;

import com.mokugyo.cokemod.init.ModCreativeTab;
import com.mokugyo.cokemod.init.ModEvents;
import com.mokugyo.cokemod.init.ModItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Cokemod.MOD_ID)
public final class Cokemod {
    public static final String MOD_ID = "cokemod";

    public Cokemod(IEventBus modEventBus) {
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTab.TABS.register(modEventBus);

        NeoForge.EVENT_BUS.addListener(ModEvents::onFurnaceFuelBurnTime);

    }
}

