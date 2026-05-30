//? fabric {
package com.bizcub.extraAnimations.main.platform;

import com.bizcub.extraAnimations.network.AnimationPayloadC2S;
import com.bizcub.extraAnimations.network.AnimationPayloadS2C;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class Fabric implements ModInitializer {

    @Override
    public void onInitialize() {
        /*~ if >=26.1 'playC2S' -> 'serverboundPlay'*/ /*~ if >=26.1 'playS2C' -> 'clientboundPlay' {*/
        PayloadTypeRegistry.serverboundPlay().register(AnimationPayloadC2S.TYPE, AnimationPayloadC2S.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(AnimationPayloadS2C.TYPE, AnimationPayloadS2C.CODEC);
        /*~}*/

        ServerPlayNetworking.registerGlobalReceiver(AnimationPayloadC2S.TYPE, (payload, context) ->
                context.server().execute(() -> context.server().getPlayerList().getPlayers().forEach(player ->
                        ServerPlayNetworking.send(player, new AnimationPayloadS2C(payload.playerUuid(), payload.animationId(), payload.isPlay()))
                ))
        );
    }
}//?}
