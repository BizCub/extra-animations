//? neoforge {
/*package com.bizcub.extraAnimations.main.platform;

import com.bizcub.extraAnimations.Main;
import com.bizcub.extraAnimations.client.Utils;
import com.bizcub.extraAnimations.network.AnimationPayloadC2S;
import com.bizcub.extraAnimations.network.AnimationPayloadS2C;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Main.MOD_ID)
public class NeoForge {

    @SubscribeEvent
    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(Main.MOD_ID);

        registrar.playToServer(AnimationPayloadC2S.TYPE, AnimationPayloadC2S.CODEC, (payload, context) -> context.enqueueWork(() ->
                PacketDistributor.sendToAllPlayers(new AnimationPayloadS2C(payload.playerUuid(), payload.animationId(), payload.isPlay()))
        ));

        registrar.playToClient(AnimationPayloadS2C.TYPE, AnimationPayloadS2C.CODEC, (payload, context) -> context.enqueueWork(() ->
                Utils.clientPayloadReceiver(payload)
        ));
    }
}*///?}
