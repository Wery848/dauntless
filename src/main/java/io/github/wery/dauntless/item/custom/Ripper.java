package io.github.wery.dauntless.item.custom;

import io.github.wery.dauntless.attachedData.SensusStat;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.ObjectUtils;

public class Ripper extends Item {
    public Ripper(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if(entity instanceof Player targetplayer) {
            // Hurt and break tool
            stack.hurtAndBreak(1, player, player.getUsedItemHand());

            // Attempt to transfer Sensus
            if(SensusStat.transferPlayerSensus(targetplayer, player, 1)) {
                // play success sound
                player.level().playSound(null, targetplayer.blockPosition(), SoundEvents.AMBIENT_SOUL_SAND_VALLEY_ADDITIONS.value(), SoundSource.VOICE);
            }
        }
        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return true;
    }
}
