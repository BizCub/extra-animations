package io.github.bizcub.extraAnimations.platform;

import io.github.bizcub.extraAnimations.Main;
import io.github.bizcub.extraAnimations.client.Utils;
import io.github.bizcub.extraAnimations.network.AnimationPayloadS2C;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = Main.MOD_ID, value = Dist.CLIENT)
public class NeoForgeClient {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> Utils.init(ClientPacketDistributor::sendToServer));
    }

    @SubscribeEvent
    public static void registerClientPayloads(RegisterClientPayloadHandlersEvent event) {
        event.register(AnimationPayloadS2C.TYPE, (payload, context) -> Utils.clientPayloadReceiver(payload));
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        Utils.onTick(minecraft);
    }

    @SubscribeEvent
    public static void onUseItem(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();
        Utils.onUseItem(event.getEntity(), stack, event.getHand());
    }
}
