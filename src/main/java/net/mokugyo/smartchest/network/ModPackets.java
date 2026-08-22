package net.mokugyo.smartchest.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModPackets {

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(
                ChangePagePacket.TYPE,
                ChangePagePacket.STREAM_CODEC,
                ChangePagePacket::handle
        );

        registrar.playToServer(
                SortRequestPacket.TYPE,
                SortRequestPacket.STREAM_CODEC,
                SortRequestPacket::handle
        );
    }
}
