package com.dfsek.terra.bukkit;

import com.dfsek.terra.api.math.vector.Location;
import com.dfsek.terra.api.platform.Player;
import com.dfsek.terra.bukkit.world.BukkitAdapter;

public class BukkitPlayer implements Player {
    private final org.bukkit.entity.Player delegate;

    public BukkitPlayer(org.bukkit.entity.Player delegate) {
        this.delegate = delegate;
    }

    @Override
    public org.bukkit.entity.Player getHandle() {
        return delegate;
    }

    @Override
    public Location getLocation() {
        org.bukkit.Location bukkit = delegate.getLocation();
        return new Location(BukkitAdapter.adapt(bukkit.getWorld()), bukkit.getX(), bukkit.getY(), bukkit.getZ());
    }

    @Override
    public void sendMessage(String message) {
        delegate.sendMessage(message);
    }
}
