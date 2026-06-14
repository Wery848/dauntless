package io.github.wery.dauntless.datagen;

import io.github.wery.dauntless.item.DauntlessItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;

public class DauntlessModelProvider extends ModelProvider {

    public DauntlessModelProvider(PackOutput output, String modId) {
        super(output, modId);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        itemModels.generateFlatItem(DauntlessItems.SENSUS.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(DauntlessItems.RIPPER.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(DauntlessItems.ACTIVATED_RIPPER.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
    }
}
