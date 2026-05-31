package com.bizcub.extraAnimations;

import net.minecraft.resources.Identifier;

public class Main {
    public static final String MOD_ID = /*$ mod_id*/ "extra_animations";

    public static final Identifier EXTRA_LAYER_ID = Identifier.fromNamespaceAndPath(Main.MOD_ID, "extra");

    public static final Identifier NONE_ANIMATION_ID = Identifier.fromNamespaceAndPath(Main.MOD_ID, "none");
    public static final Identifier LANTERN_ANIMATION_ID = Identifier.fromNamespaceAndPath(Main.MOD_ID, "lantern");
    public static final Identifier THROW_ANIMATION_ID = Identifier.fromNamespaceAndPath(Main.MOD_ID, "throw");
    public static final Identifier COMPASS_ANIMATION_ID = Identifier.fromNamespaceAndPath(Main.MOD_ID, "compass");
    public static final Identifier BRUSH_ANIMATION_ID = Identifier.fromNamespaceAndPath(Main.MOD_ID, "brush");
    public static final Identifier MAGMA_ANIMATION_ID = Identifier.fromNamespaceAndPath(Main.MOD_ID, "magma");
    public static final Identifier LEVITATION_ANIMATION_ID = Identifier.fromNamespaceAndPath(Main.MOD_ID, "levitation");
    public static final Identifier ITEM_ANIMATION_ID = Identifier.fromNamespaceAndPath(Main.MOD_ID, "item");

    public static boolean itemAnimationIsPlayed = false;
}
