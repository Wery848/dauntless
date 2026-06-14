package io.github.wery.dauntless.item;

import io.github.wery.dauntless.Dauntless;
import io.github.wery.dauntless.item.custom.ActivatedRipper;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DauntlessItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Dauntless.MODID);

    public static final DeferredItem<Item> SENSUS = ITEMS.registerSimpleItem("sensus");
    public static final DeferredItem<Item> RIPPER = ITEMS.registerItem("ripper",
            properties -> new Item(properties.stacksTo(1)));
    public static final DeferredItem<Item> ACTIVATED_RIPPER = ITEMS.registerItem("activated_ripper",
            properties -> new ActivatedRipper(properties.durability(20).stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
