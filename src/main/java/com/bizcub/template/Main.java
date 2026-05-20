package com.bizcub.template;

import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import com.zigythebird.playeranimcore.animation.layered.modifier.AbstractFadeModifier;
import com.zigythebird.playeranimcore.easing.EasingType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Avatar;

import java.util.Arrays;

public class Main {
    public static final String MOD_ID = /*$ mod_id*/ "extra_animations";

    public static final Identifier EXTRA_LAYER_ID = Identifier.fromNamespaceAndPath(Main.MOD_ID, "extra");

    public static final Identifier NONE_ANIMATION_ID = Identifier.fromNamespaceAndPath(Main.MOD_ID, "none");
    public static final Identifier LANTERN_ANIMATION_ID = Identifier.fromNamespaceAndPath(Main.MOD_ID, "lantern");
    public static final Identifier THROW_ANIMATION_ID = Identifier.fromNamespaceAndPath(Main.MOD_ID, "throw");
    public static final Identifier COMPASS_ANIMATION_ID = Identifier.fromNamespaceAndPath(Main.MOD_ID, "compass");
    public static final Identifier BRUSH_ANIMATION_ID = Identifier.fromNamespaceAndPath(Main.MOD_ID, "brush");

    public static void playAnimation(Avatar player, Identifier layer, Identifier id) {
        PlayerAnimationController controller = (PlayerAnimationController) PlayerAnimationAccess.getPlayerAnimationLayer(player, layer);
        if (controller != null) {
            if (controller.getCurrentAnimation() == null || !Arrays.stream(id.toString().split(":")).toList().getLast().equals(controller.getCurrentAnimation().animation().getNameOrId())) {
                controller.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(4, EasingType.EASE_OUT_CIRC), id);
            }
        }
    }

    public static void stopAnimation(Avatar player, Identifier layer, Identifier id) {
        PlayerAnimationController controller = (PlayerAnimationController) PlayerAnimationAccess.getPlayerAnimationLayer(player, layer);
        if (controller != null) {
            if (controller.getCurrentAnimation() != null) {
                if (id.toString().equals(MOD_ID + ":" + controller.getCurrentAnimation().animation().getNameOrId())) {
                    controller.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(4, EasingType.EASE_OUT_CIRC), NONE_ANIMATION_ID);
                }
            }
        }
    }
}
