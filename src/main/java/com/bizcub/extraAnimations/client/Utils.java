package com.bizcub.extraAnimations.client;

import com.bizcub.extraAnimations.Main;
import com.bizcub.extraAnimations.network.AnimationPayloadC2S;
import com.bizcub.extraAnimations.network.AnimationPayloadS2C;
import com.zigythebird.playeranim.animation.PlayerAnimResources;
import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import com.zigythebird.playeranim.api.PlayerAnimationFactory;
import com.zigythebird.playeranimcore.animation.layered.modifier.AbstractFadeModifier;
import com.zigythebird.playeranimcore.easing.EasingType;
import com.zigythebird.playeranimcore.enums.PlayState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

//? fabric {
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
//?} neoforge {
/*/^? >=1.21.6^/ import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.PacketDistributor;*///?}

public class Utils {

    public static void registerLayers() {
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(Main.EXTRA_LAYER_ID, 2000,
                player -> new PlayerAnimationController(player,
                        (controller, state, animSetter) -> PlayState.STOP
                )
        );
    }

    public static void sendAnimationC2S(Identifier id, boolean isPlay) {
        AnimationPayloadC2S payload = new AnimationPayloadC2S(Minecraft.getInstance().player.getUUID(), id, isPlay);
        /*? fabric*/ ClientPlayNetworking.send(payload);
        //~ if >=1.21.6 'PacketDistributor' -> 'ClientPacketDistributor'
        /*? neoforge*/ //ClientPacketDistributor.sendToServer(payload);
    }

    public static void clientPayloadReceiver(AnimationPayloadS2C payload) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        AbstractClientPlayer player = (AbstractClientPlayer) mc.level.getPlayerByUUID(payload.playerUuid());
        if (player == null) return;
        if (payload.isPlay())
            Utils.playAnimation(player, payload.animationId());
        else
            Utils.stopAnimation(player, payload.animationId());
    }

    public static void playAnimation(AbstractClientPlayer player, Identifier id) {
        PlayerAnimationController controller = (PlayerAnimationController) PlayerAnimationAccess
                .getPlayerAnimationLayer(player, Main.EXTRA_LAYER_ID);
        if (controller == null) return;

        if (controller.getCurrentAnimation() == null || !controller.getCurrentAnimation().animation().equals(PlayerAnimResources.getAnimation(id))) {
            controller.replaceAnimationWithFade(
                    AbstractFadeModifier.standardFadeIn(4, EasingType.EASE_OUT_CIRC), id
            );
        }
    }

    public static void stopAnimation(AbstractClientPlayer player, Identifier id) {
        PlayerAnimationController controller = (PlayerAnimationController) PlayerAnimationAccess
                .getPlayerAnimationLayer(player, Main.EXTRA_LAYER_ID);
        if (controller == null || controller.getCurrentAnimation() == null) return;

        if (controller.getCurrentAnimation().animation().equals(PlayerAnimResources.getAnimation(id))) {
            controller.replaceAnimationWithFade(
                    AbstractFadeModifier.standardFadeIn(4, EasingType.EASE_OUT_CIRC), Main.NONE_ANIMATION_ID
            );
        }
    }

    public static void onTick(Minecraft client) {
        var player = client.player;
        if (player == null) return;

        var item = player.getMainHandItem();
        //~ if >=1.21.9 'Items.LANTERN' -> 'ItemTags.LANTERNS'
        sendAnimationC2S(Main.LANTERN_ANIMATION_ID, item.is(ItemTags.LANTERNS) /*? <1.21.9 {*/ /*|| item.is(Items.SOUL_LANTERN) *//*?}*/);
        sendAnimationC2S(Main.COMPASS_ANIMATION_ID, item.is(ItemTags.COMPASSES));
        if (client.mouseHandler.isRightPressed()) {
            //~ if >=1.21.5 'Items.EGG' -> 'ItemTags.EGGS'
            if (!player.getCooldowns().isOnCooldown(item.getItem().getDefaultInstance()) && (item.is(ItemTags.EGGS) ||
                    item.is(Items.SNOWBALL) ||
                    item.is(Items.ENDER_EYE) ||
                    item.is(Items.SPLASH_POTION) ||
                    item.is(Items.LINGERING_POTION) ||
                    item.is(Items.EXPERIENCE_BOTTLE)
            )) {
                sendAnimationC2S(Main.THROW_ANIMATION_ID, true);
            }
            if (item.is(Items.BRUSH)) {
                sendAnimationC2S(Main.BRUSH_ANIMATION_ID, true);
            }
        } else {
            sendAnimationC2S(Main.BRUSH_ANIMATION_ID, false);
        }
    }

    public static void onUseItem(Player player, ItemStack itemStack) {
        if (!player.getCooldowns().isOnCooldown(itemStack.getItem().getDefaultInstance()) && (itemStack.is(Items.ENDER_PEARL) ||
                itemStack.is(Items.WIND_CHARGE)
        )) {
            sendAnimationC2S(Main.THROW_ANIMATION_ID, true);
        }
    }
}
