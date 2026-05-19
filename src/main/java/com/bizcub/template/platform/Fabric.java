//? fabric {
package com.bizcub.template.platform;

import com.bizcub.template.Main;
import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationFactory;
import com.zigythebird.playeranimcore.enums.PlayState;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class Fabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
//        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(Main.MOVEMENT_LAYER_ID, 1000,
//                player -> new PlayerAnimationController(player, (controller, state, animSetter) -> {
//                    return PlayState.STOP;
//                })
//        );

        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(Main.EXTRA_LAYER_ID, 2000,
                player -> new PlayerAnimationController(player,
                        (controller, state, animSetter) -> PlayState.STOP)
        );

//        ClientPlayConnectionEvents.JOIN.register((listener, sender, client) -> {
//            Main.playAnimation(client.player, Main.MOVEMENT_LAYER_ID, Main.IDLE_ANIMATION_ID);
//        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            var player = client.player;
            if (player != null) {
                var item = player.getActiveItem();
//                if (player.getDeltaMovement().x != 0 && player.getDeltaMovement().z != 0) {
//                    Main.playAnimation(player, Main.MOVEMENT_LAYER_ID, Main.WALKING_ANIMATION_ID);
//                } else {
//                    Main.playAnimation(player, Main.MOVEMENT_LAYER_ID, Main.IDLE_ANIMATION_ID);
//                }
                if (item.is(ItemTags.LANTERNS)) {
                    Main.playAnimation(player, Main.EXTRA_LAYER_ID, Main.LANTERN_ANIMATION_ID);
                } else {
                    Main.stopAnimation(player, Main.EXTRA_LAYER_ID, Main.LANTERN_ANIMATION_ID);
                }
                if (item.is(ItemTags.COMPASSES)) {
                    Main.playAnimation(player, Main.EXTRA_LAYER_ID, Main.COMPASS_ANIMATION_ID);
                } else {
                    Main.stopAnimation(player, Main.EXTRA_LAYER_ID, Main.COMPASS_ANIMATION_ID);
                }
            }
        });

        UseItemCallback.EVENT.register((player, level, hand) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (
                    stack.is(Items.EGG) ||
                    stack.is(Items.SNOWBALL) ||
                    stack.is(Items.ENDER_PEARL) ||
                    stack.is(Items.ENDER_EYE) ||
                    stack.is(Items.SPLASH_POTION) ||
                    stack.is(Items.LINGERING_POTION) ||
                    stack.is(Items.EXPERIENCE_BOTTLE) ||
                    stack.is(Items.WIND_CHARGE)
            ) {
                Main.playAnimation(player, Main.EXTRA_LAYER_ID, Main.THROW_ANIMATION_ID);
            }
            return InteractionResult.PASS;
        });
    }
}//?}
