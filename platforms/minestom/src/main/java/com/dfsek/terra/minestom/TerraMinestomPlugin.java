package com.dfsek.terra.minestom;

import com.dfsek.tectonic.loading.TypeRegistry;
import com.dfsek.terra.TerraWorld;
import com.dfsek.terra.api.language.Language;
import com.dfsek.terra.api.platform.TerraPlugin;
import com.dfsek.terra.api.platform.handle.ItemHandle;
import com.dfsek.terra.api.platform.handle.WorldHandle;
import com.dfsek.terra.api.platform.world.World;
import com.dfsek.terra.config.base.PluginConfig;
import com.dfsek.terra.registry.ConfigRegistry;

import java.io.File;
import java.util.logging.Logger;

public class TerraMinestomPlugin implements TerraPlugin {
    @Override
    public WorldHandle getWorldHandle() {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public TerraWorld getWorld(World world) {
        return null;
    }

    @Override
    public Logger getLogger() {
        return null;
    }

    @Override
    public PluginConfig getTerraConfig() {
        return null;
    }

    @Override
    public File getDataFolder() {
        return null;
    }

    @Override
    public boolean isDebug() {
        return false;
    }

    @Override
    public Language getLanguage() {
        return null;
    }

    @Override
    public ConfigRegistry getRegistry() {
        return null;
    }

    @Override
    public void reload() {

    }

    @Override
    public ItemHandle getItemHandle() {
        return null;
    }

    @Override
    public void saveDefaultConfig() {

    }

    @Override
    public String platformName() {
        return null;
    }

    @Override
    public void register(TypeRegistry registry) {

    }
}
