package net.mokugyo.smartchest.client;

import net.mokugyo.smartchest.SmartChest;
import net.mokugyo.smartchest.registry.ModItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

@EventBusSubscriber(modid = SmartChest.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientRegistryNative {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // BlockEntity レンダラーは既に ClientSetup で登録済み

        // Minecraft インスタンスから dispatcher / modelSet を取得して SmartChestItemRenderer を作成
        var dispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
        EntityModelSet modelSet = Minecraft.getInstance().getEntityModels();
        var itemRenderer = new SmartChestItemRenderer(dispatcher, modelSet);

        // ネイティブな登録 API を直接呼ぶ
        BlockEntityRenderers.registerBuiltinItemRenderer(ModItems.SMART_CHEST_ITEM.get(), itemRenderer);
    }
}
