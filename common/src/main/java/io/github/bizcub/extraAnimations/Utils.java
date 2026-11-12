package io.github.bizcub.extraAnimations;

import net.minecraft.resources.Identifier;

public class Utils {

    public static Identifier getIdentifier(String id) {
        return Identifier.fromNamespaceAndPath(Main.MOD_ID, id);
    }
}
