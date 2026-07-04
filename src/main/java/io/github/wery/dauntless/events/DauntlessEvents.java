package io.github.wery.dauntless.events;

import io.github.wery.dauntless.Dauntless;
import io.github.wery.dauntless.attachedData.SensusStat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.LevelData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = Dauntless.MODID)
public class DauntlessEvents {
    //Private Constants
    private static final int MAX_SEARCH_RADIUS_CHUNKS = 8;

    @SubscribeEvent
    public static void onLivingDeathEvent(LivingDeathEvent event) {
        if(event.getEntity() instanceof ServerPlayer player) {
            // If a player dies, his Sensus is checked,
            // if his Sensus is 0 or lower, the player suffers the "permanent Death" penalty
            if(player.getData(SensusStat.SENSUS) <= 0) {
                //player.setGameMode(GameType.SPECTATOR);
                ResourceKey<Level> dreamDim = ResourceKey.create(
                        Registries.DIMENSION,
                        Identifier.fromNamespaceAndPath(Dauntless.MODID, "dream")
                );
                ServerLevel dreamLevel = player.level();
                GlobalPos gPos = new GlobalPos(dreamDim, findSafeSurfacePos(dreamLevel, player.blockPosition()));
                ServerPlayer.RespawnConfig dreamDimRespawn = new ServerPlayer.RespawnConfig(new LevelData.RespawnData(gPos, 0.0f, 0.0f), false);
                player.setRespawnPosition(dreamDimRespawn, false);

                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        // If a player joins for the first time, the player's Sensus is set to a full 20.
        Player player = event.getEntity();
        player.setData(SensusStat.SENSUS_COOLDOWN, player.tickCount + 20);
        if(!player.hasData(SensusStat.JOINED)) {
            player.getData(SensusStat.SENSUS);
            player.setData(SensusStat.SENSUS, 20);
            player.setData(SensusStat.JOINED, 1);
            //player.getServer().sendSystemMessage(Component.literal("First join for you, Sensus is 20"));
        }
        //DEBUG AND TESTING ONLY BELOW
        //player.setData(SensusStat.SENSUS, 20);
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        // Should hopefully, copy a player sensus stat on death...
        if(event.isWasDeath()) {
            if (event.getOriginal().hasData(SensusStat.SENSUS)) {
                event.getEntity().setData(SensusStat.SENSUS, event.getOriginal().getData(SensusStat.SENSUS));
            }
            if (event.getOriginal().hasData(SensusStat.JOINED)) {
                event.getEntity().setData(SensusStat.JOINED, event.getOriginal().getData(SensusStat.JOINED));
            }
        }
    }

    // Helper methods (not events)
    private static BlockPos findSafeSurfacePos(ServerLevel level, BlockPos originPos) {
        int originX = originPos.getX();
        int originZ = originPos.getZ();

        for (int radius = 0; radius <= MAX_SEARCH_RADIUS_CHUNKS; radius++) {
            BlockPos found = searchRingForSurface(level, originX, originZ, radius);
            if (found != null) {
                return found;
            }
        }

        // Exhausted the search radius with no ground found anywhere - true worst-case fallback
        return originPos;
    }

    private static BlockPos searchRingForSurface(ServerLevel level, int originX, int originZ, int radius) {
        if (radius == 0) {
            return tryColumn(level, originX, originZ);
        }

        int step = 16; // one chunk width
        int offset = radius * step;

        // Walk the perimeter of the square ring at this chunk-radius, so we only
        // check *new* columns rather than re-scanning ones already tried at smaller radii
        for (int dx = -offset; dx <= offset; dx += step) {
            for (int dz = -offset; dz <= offset; dz += step) {
                boolean onRingEdge = Math.abs(dx) == offset || Math.abs(dz) == offset;
                if (!onRingEdge) continue;

                BlockPos found = tryColumn(level, originX + dx, originZ + dz);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private static BlockPos tryColumn(ServerLevel level, int x, int z) {
        level.getChunk(x >> 4, z >> 4); // force load/generate so heightmap data is accurate

        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        if (surfaceY <= level.getMinY()) {
            return null; // no ground in this column - keep searching outward
        }

        return new BlockPos(x, surfaceY, z);
    }
}
