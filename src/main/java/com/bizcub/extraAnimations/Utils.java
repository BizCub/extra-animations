package com.bizcub.extraAnimations;

import net.minecraft.resources.Identifier;

public class Utils {

    public static Identifier getIdentifier(String id) {
        return
                /*? >=1.21 {*/ Identifier.fromNamespaceAndPath(
                /*?} else*/ //new Identifier(
                Main.MOD_ID, id);
    }
}
