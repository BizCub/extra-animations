package com.bizcub.template;

import com.mojang.blaze3d.platform.InputConstants;
import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.Identifier;

public class Main {
    public static final String MOD_ID = /*$ mod_id*/ "extra_animations";
    public static final Identifier ANIMATION_LAYER_ID = Identifier.fromNamespaceAndPath(Main.MOD_ID, "animation.model.open_arms");

    public static final KeyMapping OPEN_SCREEN = new KeyMapping(
            "key." + MOD_ID + ".open_game_start_screen",
            InputConstants.KEY_Y,
            KeyMapping.Category.MISC
    );

    public static void playAnimation(AbstractClientPlayer player, Identifier id) {
        PlayerAnimationController controller = (PlayerAnimationController) PlayerAnimationAccess.getPlayerAnimationLayer(player, ANIMATION_LAYER_ID);
        if(controller == null) {
            return;
        }
        controller.triggerAnimation(id);
    }
}
