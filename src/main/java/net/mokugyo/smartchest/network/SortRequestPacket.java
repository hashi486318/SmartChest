package net.mokugyo.smartchest.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.mokugyo.smartchest.menu.SmartChestMenu;
import net.mokugyo.smartchest.util.SmartChestSort;

public record SortRequestPacket() implements CustomPacketPayload {

    public static final SortRequestPacket INSTANCE = new SortRequestPacket();
    public static final Type<SortRequestPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("smartchest", "sort_request"));

    public static final StreamCodec<FriendlyByteBuf, SortRequestPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SortRequestPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player && player.containerMenu instanceof SmartChestMenu menu) {
                if (menu.getBlockEntity() != null) {
                    SmartChestSort.sortPage(menu.getBlockEntity().getInventory(), menu.getCurrentPage());
                    menu.broadcastChanges();
                }
            }
        });
    }
}
