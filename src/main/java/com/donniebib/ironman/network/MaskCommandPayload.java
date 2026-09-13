package com.donniebib.ironman.network;

import com.donniebib.ironman.IronManMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record MaskCommandPayload(byte actionId) implements CustomPacketPayload {
    public static final Type<MaskCommandPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(IronManMod.MOD_ID, "mask_command")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, MaskCommandPayload> CODEC =
            StreamCodec.composite(ByteBufCodecs.BYTE, MaskCommandPayload::actionId, MaskCommandPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
