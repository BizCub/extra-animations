package io.github.bizcub.extraAnimations.client;

import io.github.bizcub.extraAnimations.Main;
import io.github.bizcub.extraAnimations.network.AnimationPayloadC2S;
import io.github.bizcub.extraAnimations.network.AnimationPayloadS2C;
import com.zigythebird.playeranim.animation.PlayerAnimResources;
import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import com.zigythebird.playeranim.api.PlayerAnimationFactory;
import com.zigythebird.playeranimcore.animation.layered.modifier.AbstractFadeModifier;
import com.zigythebird.playeranimcore.easing.EasingType;
import com.zigythebird.playeranimcore.enums.PlayState;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class Utils {

    private static final Map<Identifier, Boolean> LAST_SENT_STATES = new HashMap<>();
    private static Player lastStatePlayer;
    private static Consumer<AnimationPayloadC2S> packetSender;

    public static void init(Consumer<AnimationPayloadC2S> sender) {
        packetSender = sender;
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(Main.EXTRA_LAYER_ID, 2000,
                player -> new PlayerAnimationController(player,
                        (controller, state, animSetter) -> PlayState.STOP
                )
        );
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(Main.LANTERN_OFFHAND_LAYER_ID, 2001,
                player -> new PlayerAnimationController(player,
                        (controller, state, animSetter) -> PlayState.STOP
                )
        );
    }

    public static void sendAnimationC2S(Identifier id, boolean isPlay) {
        AnimationPayloadC2S payload = new AnimationPayloadC2S(Minecraft.getInstance().player.getUUID(), id, isPlay);
        packetSender.accept(payload);
    }

    public static void sendAnimationStateC2S(Identifier id, boolean isPlay) {
        Boolean previousState = LAST_SENT_STATES.put(id, isPlay);
        if (previousState == null || previousState != isPlay) {
            sendAnimationC2S(id, isPlay);
        }
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
                .getPlayerAnimationLayer(player, isOffhandAnimation(id) ? Main.LANTERN_OFFHAND_LAYER_ID : Main.EXTRA_LAYER_ID);
        if (controller == null) return;

        if (controller.getCurrentAnimation() == null || !controller.getCurrentAnimation().animation().equals(PlayerAnimResources.getAnimation(id))) {
            controller.replaceAnimationWithFade(
                    AbstractFadeModifier.standardFadeIn(4, EasingType.EASE_OUT_CIRC), id
            );
        }
    }


    private static boolean isOffhandAnimation(Identifier id) {
        return id.equals(Main.LANTERN_OFFHAND_ANIMATION_ID)
                || id.equals(Main.COMPASS_OFFHAND_ANIMATION_ID)
                || id.equals(Main.BRUSH_OFFHAND_ANIMATION_ID)
                || id.equals(Main.THROW_OFFHAND_ANIMATION_ID);
    }

    public static void stopAnimation(AbstractClientPlayer player, Identifier id) {
        PlayerAnimationController controller = (PlayerAnimationController) PlayerAnimationAccess
                .getPlayerAnimationLayer(player, isOffhandAnimation(id) ? Main.LANTERN_OFFHAND_LAYER_ID : Main.EXTRA_LAYER_ID);
        if (controller == null || controller.getCurrentAnimation() == null) return;

        if (controller.getCurrentAnimation().animation().equals(PlayerAnimResources.getAnimation(id))) {
            controller.replaceAnimationWithFade(
                    AbstractFadeModifier.standardFadeIn(4, EasingType.EASE_OUT_CIRC), Main.NONE_ANIMATION_ID
            );
        }
    }

    public static void onTick(Minecraft client) {
        var player = client.player;
        if (player == null) {
            LAST_SENT_STATES.clear();
            lastStatePlayer = null;
            return;
        }

        if (lastStatePlayer != player) {
            LAST_SENT_STATES.clear();
            lastStatePlayer = player;
        }

        if (player.hurtTime == 9
                && player.getLastDamageSource() != null
                && player.getLastDamageSource().is(DamageTypeTags.IS_FIRE)
        ) {
            sendAnimationC2S(Main.MAGMA_ANIMATION_ID, true);
        }

        sendAnimationStateC2S(Main.LEVITATION_ANIMATION_ID, player.hasEffect(MobEffects.LEVITATION));
        sendAnimationStateC2S(Main.SLOW_FALLING_ANIMATION_ID, player.hasEffect(MobEffects.SLOW_FALLING) && !player.onGround() && !player.getAbilities().flying);

        if (player.getInventory().contains(itemStack -> itemStack.is(Items.DIAMOND)) && !Main.itemAnimationIsPlayed) {
            sendAnimationC2S(Main.ITEM_ANIMATION_ID, true);
            Main.itemAnimationIsPlayed = true;
        }

        float bodyHeight = player.getBbHeight();
        double waterHeight = player.getFluidHeight(FluidTags.WATER);
        double submersionPercent = waterHeight / bodyHeight;
        sendAnimationStateC2S(Main.WATER_ANIMATION_ID, player.isInWater()
                && submersionPercent < 0.8
                && submersionPercent > 0.5
        );

        var item = player.getMainHandItem();
        sendAnimationStateC2S(Main.LANTERN_ANIMATION_ID, item.is(BlockItemTags.LANTERNS.item()));

        var offhandItem = player.getOffhandItem();
        sendAnimationStateC2S(Main.LANTERN_OFFHAND_ANIMATION_ID, offhandItem.is(BlockItemTags.LANTERNS.item()));
        sendAnimationStateC2S(Main.COMPASS_ANIMATION_ID, item.is(ItemTags.COMPASSES));
        sendAnimationStateC2S(Main.COMPASS_OFFHAND_ANIMATION_ID, offhandItem.is(ItemTags.COMPASSES));
        sendAnimationStateC2S(Main.BRUSH_ANIMATION_ID, client.mouseHandler.isRightPressed() && item.is(Items.BRUSH));
        sendAnimationStateC2S(Main.BRUSH_OFFHAND_ANIMATION_ID, client.mouseHandler.isRightPressed() && offhandItem.is(Items.BRUSH));
    }

    public static void onUseItem(Player player, ItemStack item, InteractionHand hand) {
        Identifier throwAnimationId = hand == InteractionHand.OFF_HAND
                ? Main.THROW_OFFHAND_ANIMATION_ID
                : Main.THROW_ANIMATION_ID;
        if (!player.getCooldowns().isOnCooldown(item.getItem().getDefaultInstance()) && (item.is(Items.ENDER_PEARL) ||
                item.is(Items.WIND_CHARGE)
        )) {
            sendAnimationC2S(throwAnimationId, true);
        }
        if (!player.getCooldowns().isOnCooldown(item.getItem().getDefaultInstance()) && (item.is(ItemTags.EGGS)
                || item.is(Items.SNOWBALL)
                || item.is(Items.ENDER_EYE)
                || item.is(Items.SPLASH_POTION)
                || item.is(Items.LINGERING_POTION)
                || item.is(Items.EXPERIENCE_BOTTLE)
        )) {
            sendAnimationC2S(throwAnimationId, true);
        }
    }
}
