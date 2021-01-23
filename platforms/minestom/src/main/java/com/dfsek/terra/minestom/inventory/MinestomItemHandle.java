package com.dfsek.terra.minestom.inventory;

import com.dfsek.terra.api.platform.block.MaterialData;
import com.dfsek.terra.api.platform.handle.ItemHandle;
import com.dfsek.terra.api.platform.inventory.ItemStack;
import com.dfsek.terra.api.platform.inventory.item.Enchantment;

import java.util.Set;

public class MinestomItemHandle implements ItemHandle {
    @Override
    public ItemStack newItemStack(MaterialData material, int amount) {
        return null;
    }

    @Override
    public Enchantment getEnchantment(String id) {
        return null;
    }

    @Override
    public Set<Enchantment> getEnchantments() {
        return null;
    }
}
