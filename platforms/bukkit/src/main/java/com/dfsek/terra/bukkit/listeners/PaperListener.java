package com.dfsek.terra.bukkit.listeners;

import com.dfsek.terra.TerraWorld;
import com.dfsek.terra.api.platform.TerraPlugin;
import com.dfsek.terra.async.AsyncStructureFinder;
import com.dfsek.terra.bukkit.world.BukkitAdapter;
import com.dfsek.terra.debug.Debug;
import com.dfsek.terra.population.items.TerraStructure;
import io.papermc.paper.event.world.StructureLocateEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Placeholder, will be used once Paper accepts StructureLocateEvent PR.
 */
public class PaperListener implements Listener {
    private final TerraPlugin main;

    public PaperListener(TerraPlugin main) {
        this.main = main;
    }

    @EventHandler
    public void onStructureLocate(StructureLocateEvent e) {
        e.setResult(null); // Assume no result.
        String name = "minecraft:" + e.getType().getName();
        if(!TerraWorld.isTerraWorld(BukkitAdapter.adapt(e.getWorld()))) return;
        Debug.info("Overriding structure location for \"" + name + "\"");
        TerraWorld tw = main.getWorld(BukkitAdapter.adapt(e.getWorld()));
        TerraStructure config = tw.getConfig().getStructure(tw.getConfig().getTemplate().getLocatable().get(name));
        if(config != null) {
            AsyncStructureFinder finder = new AsyncStructureFinder(tw.getGrid(), config, BukkitAdapter.adapt(e.getOrigin()), 0, 500, location -> {
                if(location != null)
                    e.setResult(BukkitAdapter.adapt(location.toLocation(BukkitAdapter.adapt(e.getWorld()))));
                Debug.info("Location: " + location);
            }, main);
            finder.run(); // Do this synchronously.
        } else {
            main.getLogger().warning("No overrides are defined for \"" + name + "\"");
        }

    }


}
