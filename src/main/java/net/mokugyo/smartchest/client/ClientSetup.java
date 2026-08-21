package net.mokugyo.smartchest.client;

import net.mokugyo.smartchest.SmartChest;
import net.mokugyo.smartchest.registry.ModBlockEntities;
import net.mokugyo.smartchest.registry.ModMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = SmartChest.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.SMART_CHEST_MENU.get(), SmartChestScreen::new);
    }

    // ★ 内部クラスに包まず、トップレベルの静的メソッドとして @SubscribeEvent を付ける
    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SmartChestModel.LAYER_LOCATION, SmartChestModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                ModBlockEntities.SMART_CHEST_BLOCKENTITY.get(),
                SmartChestRenderer::new
        );
    }
}