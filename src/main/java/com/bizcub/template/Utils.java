package com.bizcub.template;

import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationFactory;
import com.zigythebird.playeranimcore.enums.PlayState;
import net.minecraft.client.Minecraft;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class Utils {

    public static void registerLayers() {
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(Main.EXTRA_LAYER_ID, 2000,
                player -> new PlayerAnimationController(player,
                        (controller, state, animSetter) -> PlayState.STOP
                )
        );
    }

    public static void onTick(Minecraft client) {
        var player = client.player;
        if (player != null) {
            var item = player.getActiveItem();
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
            if (client.mouseHandler.isRightPressed()) {
                if (!player.getCooldowns().isOnCooldown(item.getItem().getDefaultInstance()) && (item.is(ItemTags.EGGS) ||
                        item.is(Items.SNOWBALL) ||
                        item.is(Items.ENDER_EYE) ||
                        item.is(Items.SPLASH_POTION) ||
                        item.is(Items.LINGERING_POTION) ||
                        item.is(Items.EXPERIENCE_BOTTLE)
                )) {
                    Main.playAnimation(player, Main.EXTRA_LAYER_ID, Main.THROW_ANIMATION_ID);
                }
                if (item.is(Items.BRUSH)) {
                    Main.playAnimation(player, Main.EXTRA_LAYER_ID, Main.BRUSH_ANIMATION_ID);
                }
            } else {
                Main.stopAnimation(player, Main.EXTRA_LAYER_ID, Main.BRUSH_ANIMATION_ID);
            }
        }
    }

    public static void onUseItem(Player player, ItemStack itemStack) {
        if (!player.getCooldowns().isOnCooldown(itemStack.getItem().getDefaultInstance()) && (itemStack.is(Items.ENDER_PEARL) ||
                itemStack.is(Items.WIND_CHARGE)
        )) {
            Main.playAnimation(player, Main.EXTRA_LAYER_ID, Main.THROW_ANIMATION_ID);
        }
    }
}
