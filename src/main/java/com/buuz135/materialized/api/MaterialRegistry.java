package com.buuz135.materialized.api;

import com.buuz135.materialized.Materialized;
import com.buuz135.materialized.api.block.MaterializedBlock;
import com.buuz135.materialized.api.item.MaterializedItem;
import com.buuz135.materialized.api.material.BlockMaterial;
import com.buuz135.materialized.api.material.CreatedMaterial;
import com.buuz135.materialized.api.material.ItemMaterial;
import com.buuz135.materialized.api.material.info.MaterialInfo;
import com.buuz135.materialized.utils.Reference;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.logging.log4j.Level;

import java.util.HashMap;

public class MaterialRegistry {

    public static final MaterialRegistry INSTANCE = new MaterialRegistry();

    private HashMap<String, BlockMaterial> blockMaterials;
    private HashMap<String, ItemMaterial> itemMaterials;
    private HashMap<String, CreatedMaterial> materials;

    public MaterialRegistry() {
        materials = new HashMap<>();
        itemMaterials = new HashMap<>();
        this.addItemMaterial(new ItemMaterial("ingot", new ResourceLocation(Reference.MODID, "items/metalingot"), 0));
        this.addItemMaterial(new ItemMaterial("nugget", new ResourceLocation(Reference.MODID, "items/metalnugget"), 0));
        this.addItemMaterial(new ItemMaterial("dust", new ResourceLocation(Reference.MODID, "items/metaldust"), 0));
        this.addItemMaterial(new ItemMaterial("tinydust", new ResourceLocation(Reference.MODID, "items/metaltinydust"), 0));
        this.addItemMaterial(new ItemMaterial("plate", new ResourceLocation(Reference.MODID, "items/metalplate"), 0));
        this.addItemMaterial(new ItemMaterial("gear", new ResourceLocation(Reference.MODID, "items/metalgear"), 0));
        blockMaterials = new HashMap<>();
        this.addBlockMaterial(new BlockMaterial("ore", Material.ROCK, "pickaxe", new ResourceLocation(Reference.MODID, "blocks/metalore"), 0));
        this.addBlockMaterial(new BlockMaterial("denseore", Material.ROCK, "pickaxe", new ResourceLocation(Reference.MODID, "blocks/metaldenseore"), 0));
        this.addBlockMaterial(new BlockMaterial("lightore", Material.ROCK, "pickaxe", new ResourceLocation(Reference.MODID, "blocks/metallightore"), 0));
        this.addBlockMaterial(new BlockMaterial("block", Material.IRON, "pickaxe", new ResourceLocation(Reference.MODID, "blocks/metalblock"), 0));

    }

    public CreatedMaterial addMaterial(MaterialInfo materialInfo) {
        if (!Loader.instance().isInState(LoaderState.PREINITIALIZATION)) {
            ModContainer modContainer = Loader.instance().activeModContainer();
            String modContainerName = modContainer == null ? null : modContainer.getName();
            Materialized.LOGGER.log(Level.ERROR, "Trying to get created material {} too soon. Call it after the PREINITIALIZATION. Mod: {}", materialInfo.getName(), modContainerName);
            return null;
        }
        CreatedMaterial createdMaterial = getMaterialOrCreate(materialInfo.getName(), (int) Long.parseLong(materialInfo.getColor(), 16));
        materialInfo.getBlockParts().forEach(blockPart -> createdMaterial.createBlock(getBlockMaterial(blockPart.getType()), blockPart));
        materialInfo.getItemParts().forEach(itemPart -> createdMaterial.createItem(getItemMaterial(itemPart.getType()), itemPart));
        if (!materials.containsKey(materialInfo.getName())) materials.put(materialInfo.getName(), createdMaterial);
        return createdMaterial;
    }


    public CreatedMaterial getMaterial(String name) {
        if (materials.containsKey(name)) return materials.get(name);
        return null;
    }

    private CreatedMaterial getMaterialOrCreate(String name, int color) {
        CreatedMaterial material = getMaterial(name);
        return material == null ? new CreatedMaterial(name, color) : material;
    }

    public void addBlockMaterial(BlockMaterial blockMaterial) {
        if (!blockMaterials.containsKey(blockMaterial.getName())) {
            blockMaterials.put(blockMaterial.getName(), blockMaterial);
        }
    }

    public BlockMaterial getBlockMaterial(String name) {
        if (blockMaterials.containsKey(name)) {
            return blockMaterials.get(name);
        }
        return null;
    }

    private void addItemMaterial(ItemMaterial itemMaterial) {
        if (!itemMaterials.containsKey(itemMaterial.getName())) {
            itemMaterials.put(itemMaterial.getName(), itemMaterial);
        }
    }

    private ItemMaterial getItemMaterial(String type) {
        if (itemMaterials.containsKey(type)) {
            return itemMaterials.get(type);
        }
        return null;
    }

    public MaterializedItem getItem(String name, String type) {
        CreatedMaterial material = getMaterial(name);
        if (material == null) return null;
        ItemMaterial materialType = getItemMaterial(type);
        if (type == null) return null;
        return material.getItem(materialType);
    }

    public MaterializedBlock getBlock(String name, String type) {
        CreatedMaterial material = getMaterial(name);
        if (material == null) return null;
        BlockMaterial materialType = getBlockMaterial(type);
        if (type == null) return null;
        return material.getBlock(materialType);
    }

    public HashMap<String, CreatedMaterial> getMaterials() {
        return materials;
    }
}
