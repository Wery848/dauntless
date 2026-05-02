package io.github.wery.dauntless.events;

import io.github.wery.dauntless.Dauntless;
import io.github.wery.dauntless.attachedData.SensusStat;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = Dauntless.MODID)
public class DauntlessEvents {
    @SubscribeEvent
    public static void onLivingDeathEvent(LivingDeathEvent event) {
        if(event.getEntity() instanceof ServerPlayer) {
            // If a player dies, his Sensus is checked,
            // if his Sensus is 0 or lower, the player is set to spectator mode
            ServerPlayer player = (ServerPlayer) event.getEntity();
            if(player.getData(SensusStat.SENSUS) <= 0) {
            PlayerList playerlist = player.level().getServer().getPlayerList();
                playerlist.getPlayer(player.getUUID()).setGameMode(GameType.SPECTATOR);
                //player.getServer().sendSystemMessage(Component.literal("Hey there, you should be in spectator mode now."));
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
}
