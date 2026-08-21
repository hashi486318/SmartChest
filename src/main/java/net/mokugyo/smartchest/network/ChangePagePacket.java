package net.mokugyo.smartchest.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.mokugyo.smartchest.menu.SmartChestMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ChangePagePacket(int newPage) implements CustomPacketPayload {
    public static final Type<ChangePagePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("smartchest", "change_page"));
    public static final StreamCodec<FriendlyByteBuf, ChangePagePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ChangePagePacket::newPage, ChangePagePacket::new
    );

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(ChangePagePacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player && player.containerMenu instanceof SmartChestMenu menu) {
                menu.setPage(msg.newPage());
            }
        });
    }
}