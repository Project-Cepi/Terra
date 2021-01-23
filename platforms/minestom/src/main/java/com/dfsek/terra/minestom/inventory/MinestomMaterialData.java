package com.dfsek.terra.minestom.inventory;

import com.dfsek.terra.api.platform.block.BlockData;
import com.dfsek.terra.api.platform.block.MaterialData;
import net.minestom.server.item.Material;

public class MinestomMaterialData implements MaterialData {

    private Material material;

    @Override
    public boolean matches(MaterialData other) {
        return false;
    }

    @Override
    public boolean matches(BlockData other) {
        return false;
    }

    @Override
    public boolean isSolid() {
        return material.isBlock();
    }

    @Override
    public boolean isAir() {
        return material == Material.AIR;
    }

    @Override
    public double getMaxDurability() {
        return 0;
    }

    @Override
    public BlockData createBlockData() {
        return null;
    }

    @Override
    public Object getHandle() {
        return null;
    }
}
