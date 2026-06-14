package io.github.wery.dauntless.attachedData;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import io.github.wery.dauntless.Dauntless;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.attribute.AmbientSounds;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Random;
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

    /**
     * Determines if a player has greater than 0 Sensus remaining.
     * @param player The target player
     * @return True if the player has Sensus, false otherwise
     */
    public static boolean playerHasSensus(Player player) {
        return player.getData(SENSUS) > 0;
    }

    /**
     * Changes a player's Sensus without a cooldown.
     * @param player the target player to target the Sensus of.
     * @param amount the amount to reduce the player's Sensus by
     * @return True if the player had Sensus and it was changed, false otherwise
     */
    public static boolean changePlayerSensus(Player player, int amount) {
        if(playerHasSensus(player)) {
            player.setData(SENSUS, player.getData(SENSUS) + amount);
            return true;
        }
        return false;
    }

    /**
     * Changes player Sensus but applies a cooldown. Used for combat changes to Sensus.
     * @param player The player to change the Sensus of
     * @param amount how much Sensus to change the target player by
     * @return True if the Player's Sensus existed and was adjusted, false otherwise
     */
    public static boolean changePlayerSensusCooldown(Player player, int amount) {
        int cooldown = player.getData(SENSUS_COOLDOWN); // Default is 0
        if(playerHasSensus(player) && cooldown < player.tickCount) {
            player.setData(SENSUS_COOLDOWN, player.tickCount + 20); // 20 = 1 sec
            player.setData(SENSUS, player.getData(SENSUS) + amount);
            return true;
        }
        return false;
    }

    /**
     * Determines if a player has any sensus remaining
     * @param player the target player to check for sensus cooldown
     * @return True if the player's cooldown has run out, false otherwise
     */
    public static boolean sensusCooldownStatus(Player player) {
        return player.getData(SENSUS_COOLDOWN) < player.tickCount;
    }

    /**
     * Transfers Sensus from one player to another. Sensus is only transferred if the receiver has at least 1 Sensus.
     * @param giver The player who loses sensus to the other
     * @param receiver The player who receives sensus from the other
     * @param amount The amount of sensus to transfer
     * @return True if the Sensus was transferred, false otherwise
     */
    public static boolean transferPlayerSensus(Player giver, Player receiver, int amount) {
        if(changePlayerSensusCooldown(giver, -amount)) {
            return changePlayerSensus(receiver, amount);

        }
        return false;
    }

}
