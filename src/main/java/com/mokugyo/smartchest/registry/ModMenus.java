package com.mokugyo.smartchest.registry;

import com.mokugyo.smartchest.SmartChest;
import com.mokugyo.smartchest.blockentity.SmartChestBlockEntity;
import com.mokugyo.smartchest.menu.SmartChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {

    public static final DeferredRegister<MenuType<?>> SMART_CHEST_MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, SmartChest.MOD_ID);

    public static final RegistryObject<MenuType<SmartChestMenu>> SMART_CHEST_MENU =
            SMART_CHEST_MENUS.register("smart_chest_menu",
                    () -> IForgeMenuType.create((containerId, inv, data) -> {
                        var pos = data.readBlockPos();
                        var level = inv.player.level();

                        if (level.getBlockEntity(pos) instanceof SmartChestBlockEntity chest) {
                            int page = data.readVarInt();
                            return new SmartChestMenu(containerId, inv, chest, page);
                        }
                        return null;
                    }));
}
