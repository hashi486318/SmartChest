package net.mokugyo.smartchest.registry;

import net.mokugyo.smartchest.SmartChest;
import net.mokugyo.smartchest.blockentity.SmartChestBlockEntity;
import net.mokugyo.smartchest.menu.SmartChestMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenus {

    public static final DeferredRegister<MenuType<?>> SMART_CHEST_MENUS =
            DeferredRegister.create(Registries.MENU, SmartChest.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<SmartChestMenu>> SMART_CHEST_MENU =
            SMART_CHEST_MENUS.register("smart_chest_menu",
                    () -> IMenuTypeExtension.create((containerId, inv, data) -> {
                        // パケットからチェストの座標を読み取る
                        var pos = data.readBlockPos();
                        var level = inv.player.level();

                        // クライアント側で該当位置のBlockEntityを取得してMenuを作る
                        if (level.getBlockEntity(pos) instanceof SmartChestBlockEntity chest) {
                            return new SmartChestMenu(containerId, inv, chest);
                        }
                        return null;
                    }));
}