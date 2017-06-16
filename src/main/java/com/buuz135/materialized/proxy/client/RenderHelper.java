package com.buuz135.materialized.proxy.client;


import com.buuz135.materialized.api.material.BlockMaterial;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

import java.util.Map;

public class RenderHelper {

    public static void registerBlockModel(Block block, BlockMaterial type) {
        System.out.println(type.getName());
        ModelLoader.setCustomStateMapper(block, blockIn -> {
            Map<IBlockState, ModelResourceLocation> map = Maps.newHashMap();
            map.put(blockIn.getDefaultState(), new ModelResourceLocation(type.getResourceLocation(), "normal"));
            return map;
        });
        ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(block), new SimpleItemMeshDefinition(type.getResourceLocation(), "normal"));
    }
}
