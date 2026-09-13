package com.donniebib.ironman.network;

import com.donniebib.ironman.IronManMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.UUID;

public record HelmetStatePayload(long uuidMost, long uuidLeast, byte stateId, int progressBits)
        implements CustomPacketPayload {

    public static final Type<HelmetStatePayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(IronManMod.MOD_ID, "helmet_state")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, HelmetStatePayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.LONG, HelmetStatePayload::uuidMost,
                    ByteBufCodecs.LONG, HelmetStatePayload::uuidLeast,
                    ByteBufCodecs.BYTE, HelmetStatePayload::stateId,
                    ByteBufCodecs.INT, HelmetStatePayload::progressBits,
                    HelmetStatePayload::new
            );

    public static HelmetStatePayload of(UUID uuid, byte stateId, float progress) {
        return new HelmetStatePayload(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits(),
                stateId, Float.floatToIntBits(progress));
    }

    public UUID playerId() {
        return new UUID(uuidMost, uuidLeast);
    }

    public float progress() {
        return Float.intBitsToFloat(progressBits);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
