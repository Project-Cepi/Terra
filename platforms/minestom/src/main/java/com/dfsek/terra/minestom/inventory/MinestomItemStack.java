package com.dfsek.terra.minestom.inventory;

import com.dfsek.terra.api.platform.block.MaterialData;
import com.dfsek.terra.api.platform.inventory.ItemStack;
import com.dfsek.terra.api.platform.inventory.item.ItemMeta;

public class MinestomItemStack implements ItemStack {

    private net.minestom.server.item.ItemStack stack;

    @Override
    public int getAmount() {
        return stack.getAmount();
    }

    @Override
    public void setAmount(int i) {
        stack.setAmount((byte) i);
    }

    @Override
    public MaterialData getType() {
        return null;
    }

    @Override
    public ItemStack clone() {
        return null;
    }

    @Override
    public ItemMeta getItemMeta() {
        return null;
    }

    @Override
    public void setItemMeta(ItemMeta meta) {

    }

    @Override
    public Object getHandle() {
        return null;
    }
}
