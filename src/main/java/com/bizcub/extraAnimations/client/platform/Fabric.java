//? fabric {
package com.bizcub.extraAnimations.client.platform;

import com.bizcub.extraAnimations.client.Utils;
import com.bizcub.extraAnimations.network.AnimationPayloadS2C;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

public class Fabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Utils.registerLayers();

        ClientPlayNetworking.registerGlobalReceiver(AnimationPayloadS2C.TYPE, (payload, context) ->
                context.client().execute(() -> Utils.clientPayloadReceiver(payload))
        );

        ClientTickEvents.END_CLIENT_TICK.register(Utils::onTick);

        UseItemCallback.EVENT.register((player, level, hand) -> {
            ItemStack stack = player.getItemInHand(hand);
            Utils.onUseItem(player, stack);
            return InteractionResult.PASS;
        });
    }
}//?}
