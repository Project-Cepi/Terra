package com.dfsek.terra.config.factories;

import com.dfsek.terra.api.core.TerraPlugin;
import com.dfsek.terra.api.math.noise.samplers.FastNoiseLite;
import com.dfsek.terra.api.platform.block.BlockData;
import com.dfsek.terra.api.world.flora.Flora;
import com.dfsek.terra.api.world.palette.NoisePalette;
import com.dfsek.terra.api.world.palette.Palette;
import com.dfsek.terra.api.world.palette.holder.PaletteLayerHolder;
import com.dfsek.terra.config.templates.FloraTemplate;
import com.dfsek.terra.world.population.items.flora.TerraFlora;

public class FloraFactory implements TerraFactory<FloraTemplate, Flora> {
    @Override
    public TerraFlora build(FloraTemplate config, TerraPlugin main) {
        FastNoiseLite whiteNoise = new FastNoiseLite();
        whiteNoise.setNoiseType(FastNoiseLite.NoiseType.WhiteNoise);
        Palette<BlockData> palette = new NoisePalette<>(whiteNoise, false);
        for(PaletteLayerHolder layer : config.getFloraPalette()) {
            palette.add(layer.getLayer(), layer.getSize(), layer.getSampler());
        }
        return new TerraFlora(palette, config.doPhysics(), config.isCeiling(), config.getIrrigable(), config.getSpawnable(), config.getReplaceable(), config.getRotatable(), config.getMaxPlacements(), config.getSearch(), config.isSpawnBlacklist(), config.getIrrigableOffset(), main);
    }
}
