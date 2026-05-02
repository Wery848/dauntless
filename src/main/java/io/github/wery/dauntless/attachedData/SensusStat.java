package io.github.wery.dauntless.attachedData;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import io.github.wery.dauntless.Dauntless;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class SensusStat {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Dauntless.MODID);

    public static final Supplier<AttachmentType<Integer>> JOINED = ATTACHMENT_TYPES.register(
            "joined", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT.fieldOf("joined")).build()
    );
    public static final Supplier<AttachmentType<Integer>> SENSUS = ATTACHMENT_TYPES.register(
            "sensus", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT.fieldOf("sensus")).build()
    );
    public static final Supplier<AttachmentType<Integer>> SENSUS_COOLDOWN = ATTACHMENT_TYPES.register(
            "sensus_cooldown", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT.fieldOf("sensus_cooldown")).build()
    );

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }

    public static boolean playerHasSensus(Player player) {
        return player.getData(SENSUS) > 0;
    }

    public static boolean changePlayerSensus(Player player, int amount) {
        if(playerHasSensus(player)) {
            player.setData(SENSUS, player.getData(SENSUS) + amount);
            return true;
        }
        return false;
    }
}
