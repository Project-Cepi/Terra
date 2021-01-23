package com.dfsek.terra.minestom;

import net.minestom.server.extensions.Extension;

public class TerraBootstrapPlugin extends Extension {

    TerraMinestomPlugin plugin;

    @Override
    public void initialize() {
        this.plugin = new TerraMinestomPlugin();
    }

    @Override
    public void terminate() {

    }
}
