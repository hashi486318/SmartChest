package net.mokugyo.smartchest.client;

import net.mokugyo.smartchest.SmartChest;
import net.mokugyo.smartchest.registry.ModItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

@EventBusSubscriber(modid = SmartChest.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientRegistryNative {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // BlockEntity レンダラーは既に ClientSetup で登録済み

        BlockEntityRenderDispatcher dispatcher = event.getBlockEntityRenderDispatcher();
        EntityModelSet modelSet = event.getEntityModelSet();
        var itemRenderer = new SmartChestItemRenderer(dispatcher, modelSet);

        // ネイティブな登録 API を直接呼ぶ（Fabric API を使わない）
        BlockEntityRenderers.registerBuiltinItemRenderer(ModItems.SMART_CHEST_ITEM.get(), itemRenderer);
    }
}
