package com.mokugyo.smartchest.network;

import com.mokugyo.smartchest.menu.SmartChestMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ChangePagePacket(int newPage) {

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.newPage);
    }

    public static ChangePagePacket decode(FriendlyByteBuf buf) {
        return new ChangePagePacket(buf.readInt());
    }

    public static void handle(ChangePagePacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.containerMenu instanceof SmartChestMenu menu) {
                menu.setPage(msg.newPage());
            }
        });
        context.setPacketHandled(true);
    }
}
