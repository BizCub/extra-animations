package com.bizcub.extraAnimations.network;

import com.bizcub.extraAnimations.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.UUID;

public record AnimationPayloadC2S(UUID playerUuid, Identifier animationId, boolean isPlay) implements CustomPacketPayload {
    public static final Type<AnimationPayloadC2S> TYPE = new Type<>(Utils.getIdentifier("play_animation_c2s"));

    public static final StreamCodec<ByteBuf, AnimationPayloadC2S> CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC, AnimationPayloadC2S::playerUuid,
                    Identifier.STREAM_CODEC, AnimationPayloadC2S::animationId,
                    ByteBufCodecs.BOOL, AnimationPayloadC2S::isPlay,
                    AnimationPayloadC2S::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
