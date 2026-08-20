package com.mokugyo.cokemod.init;

import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;

public final class ModEvents {

    public static void onFurnaceFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        if (event.getItemStack().is(ModItems.COKE.get())) {
            event.setBurnTime(3200);
        } else if (event.getItemStack().is(ModItems.COKE_MASS.get())) {
            event.setBurnTime(32000);
        }
    }

    private ModEvents() {}
}