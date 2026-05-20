//? fabric {
package com.bizcub.template.platform;

import com.bizcub.template.Utils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

public class Fabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Utils.registerLayers();

        ClientTickEvents.END_CLIENT_TICK.register(Utils::onTick);

        UseItemCallback.EVENT.register((player, level, hand) -> {
            ItemStack stack = player.getItemInHand(hand);
            Utils.onUseItem(player, stack);
            return InteractionResult.PASS;
        });
    }
}//?}
