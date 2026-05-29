package com.bizcub.extraAnimations.network;

import com.bizcub.extraAnimations.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.UUID;

public record AnimationPayloadS2C(UUID playerUuid, Identifier animationId, boolean isPlay) implements CustomPacketPayload {
    public static final Type<AnimationPayloadS2C> TYPE = new Type<>(Utils.getIdentifier("play_animation_s2c"));

    public static final StreamCodec<ByteBuf, AnimationPayloadS2C> CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC, AnimationPayloadS2C::playerUuid,
                    Identifier.STREAM_CODEC, AnimationPayloadS2C::animationId,
                    ByteBufCodecs.BOOL, AnimationPayloadS2C::isPlay,
                    AnimationPayloadS2C::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
