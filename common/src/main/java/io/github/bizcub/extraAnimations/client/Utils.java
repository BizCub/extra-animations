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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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

    private static final Map<Identifier, Identifier> LAST_SENT_ANIMATIONS = new HashMap<>();
    private static final Set<Identifier> PENDING_ANIMATION_LAYERS = new HashSet<>();
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
        if (isPlay) {
            Identifier layer = isOffhandAnimation(id) ? Main.LANTERN_OFFHAND_LAYER_ID : Main.EXTRA_LAYER_ID;
            LAST_SENT_ANIMATIONS.put(layer, id);
            PENDING_ANIMATION_LAYERS.add(layer);
        }
        AnimationPayloadC2S payload = new AnimationPayloadC2S(Minecraft.getInstance().player.getUUID(), id, isPlay);
        packetSender.accept(payload);
    }

    private static void sendSelectedAnimation(Identifier layer, Identifier id) {
        Identifier previous = LAST_SENT_ANIMATIONS.get(layer);
        if (previous != null && (previous.equals(Main.MAGMA_ANIMATION_ID) || previous.equals(Main.ITEM_ANIMATION_ID)
                || previous.equals(Main.THROW_ANIMATION_ID) || previous.equals(Main.THROW_OFFHAND_ANIMATION_ID))) {
            if (PENDING_ANIMATION_LAYERS.contains(layer)) return;
            PlayerAnimationController controller = (PlayerAnimationController) PlayerAnimationAccess
                    .getPlayerAnimationLayer(Minecraft.getInstance().player, layer);
            if (controller == null || !controller.hasAnimationFinished()) return;
        }
        if (java.util.Objects.equals(previous, id)) return;
        if (previous != null) sendAnimationC2S(previous, false);
        LAST_SENT_ANIMATIONS.remove(layer);
        if (id != null) sendAnimationC2S(id, true);
    }

    public static void clientPayloadReceiver(AnimationPayloadS2C payload) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        AbstractClientPlayer player = (AbstractClientPlayer) mc.level.getPlayerByUUID(payload.playerUuid());
        if (player == null) return;
        Identifier layer = isOffhandAnimation(payload.animationId()) ? Main.LANTERN_OFFHAND_LAYER_ID : Main.EXTRA_LAYER_ID;
        if (player == mc.player && payload.isPlay() && payload.animationId().equals(LAST_SENT_ANIMATIONS.get(layer))) {
            PENDING_ANIMATION_LAYERS.remove(layer);
        }
        if (payload.isPlay())
            Utils.playAnimation(player, payload.animationId());
        else
            Utils.stopAnimation(player, payload.animationId());
    }

    public static void playAnimation(AbstractClientPlayer player, Identifier id) {
        PlayerAnimationController controller = (PlayerAnimationController) PlayerAnimationAccess
                .getPlayerAnimationLayer(player, isOffhandAnimation(id) ? Main.LANTERN_OFFHAND_LAYER_ID : Main.EXTRA_LAYER_ID);
        if (controller == null) return;

        if (controller.getCurrentAnimation() == null || controller.hasAnimationFinished()
                || !controller.getCurrentAnimation().animation().equals(PlayerAnimResources.getAnimation(id))) {
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
            LAST_SENT_ANIMATIONS.clear();
            PENDING_ANIMATION_LAYERS.clear();
            lastStatePlayer = null;
            return;
        }

        if (lastStatePlayer != player) {
            LAST_SENT_ANIMATIONS.clear();
            PENDING_ANIMATION_LAYERS.clear();
            lastStatePlayer = player;
        }

        if (player.hurtTime == 9
                && player.getLastDamageSource() != null
                && player.getLastDamageSource().is(DamageTypeTags.IS_FIRE)
        ) {
            sendAnimationC2S(Main.MAGMA_ANIMATION_ID, true);
        }

        if (player.getInventory().contains(itemStack -> itemStack.is(Items.DIAMOND)) && !Main.itemAnimationIsPlayed) {
            sendAnimationC2S(Main.ITEM_ANIMATION_ID, true);
            Main.itemAnimationIsPlayed = true;
        }

        float bodyHeight = player.getBbHeight();
        double waterHeight = player.getFluidHeight(FluidTags.WATER);
        double submersionPercent = waterHeight / bodyHeight;
        var item = player.getMainHandItem();
        var offhandItem = player.getOffhandItem();
        Identifier animation = null;
        if (client.mouseHandler.isRightPressed() && item.is(Items.BRUSH)) animation = Main.BRUSH_ANIMATION_ID;
        else if (player.hasEffect(MobEffects.LEVITATION)) animation = Main.LEVITATION_ANIMATION_ID;
        else if (player.hasEffect(MobEffects.SLOW_FALLING) && !player.onGround() && !player.getAbilities().flying) animation = Main.SLOW_FALLING_ANIMATION_ID;
        else if (player.isInWater() && submersionPercent > 0.5 && submersionPercent < 0.8) animation = Main.WATER_ANIMATION_ID;
        else if (item.is(BlockItemTags.LANTERNS.item())) animation = Main.LANTERN_ANIMATION_ID;
        else if (item.is(ItemTags.COMPASSES)) animation = Main.COMPASS_ANIMATION_ID;
        sendSelectedAnimation(Main.EXTRA_LAYER_ID, animation);

        Identifier offhandAnimation = null;
        if (client.mouseHandler.isRightPressed() && offhandItem.is(Items.BRUSH)) offhandAnimation = Main.BRUSH_OFFHAND_ANIMATION_ID;
        else if (offhandItem.is(BlockItemTags.LANTERNS.item())) offhandAnimation = Main.LANTERN_OFFHAND_ANIMATION_ID;
        else if (offhandItem.is(ItemTags.COMPASSES)) offhandAnimation = Main.COMPASS_OFFHAND_ANIMATION_ID;
        sendSelectedAnimation(Main.LANTERN_OFFHAND_LAYER_ID, offhandAnimation);
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
