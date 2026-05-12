//? fabric {
package com.bizcub.template.platform;

import com.bizcub.template.Main;
import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationFactory;
import com.zigythebird.playeranimcore.enums.PlayState;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.world.InteractionResult;

public class Fabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        KeyMappingHelper.registerKeyMapping(Main.OPEN_SCREEN);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (Main.OPEN_SCREEN.consumeClick()) {
                Main.playAnimation(client.player, Main.ANIMATION_LAYER_ID);
            }
        });

        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(Main.ANIMATION_LAYER_ID, 1000,
                player -> new PlayerAnimationController(player,
                        (controller, state, animSetter) -> PlayState.STOP
                )
        );

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitEntity) -> {
            Main.playAnimation(player, Main.ANIMATION_LAYER_ID);
            return InteractionResult.PASS;
        });
    }
}//?}
