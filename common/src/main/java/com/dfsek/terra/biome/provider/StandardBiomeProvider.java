package com.dfsek.terra.biome.provider;

import com.dfsek.tectonic.exception.ConfigException;
import com.dfsek.terra.api.core.TerraPlugin;
import com.dfsek.terra.api.math.noise.NoiseSampler;
import com.dfsek.terra.api.math.vector.Vector2;
import com.dfsek.terra.api.util.seeded.NoiseSeeded;
import com.dfsek.terra.biome.TerraBiome;
import com.dfsek.terra.biome.pipeline.BiomeHolder;
import com.dfsek.terra.biome.pipeline.BiomePipeline;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.jafama.FastMath;
import org.jetbrains.annotations.NotNull;

public class StandardBiomeProvider implements BiomeProvider {
    private final LoadingCache<Vector2, BiomeHolder> holderCache;
    private final BiomePipeline pipeline;
    private int resolution = 1;
    private final NoiseSampler mutator;
    private final double noiseAmp;
    private final int seed;

    protected StandardBiomeProvider(BiomePipeline pipeline, TerraPlugin main, NoiseSampler mutator, double noiseAmp, int seed) {
        this.mutator = mutator;
        this.noiseAmp = noiseAmp;
        this.seed = seed;
        holderCache = CacheBuilder.newBuilder()
                .maximumSize(main == null ? 32 : main.getTerraConfig().getProviderCache())
                .build(
                        new CacheLoader<Vector2, BiomeHolder>() {
                            @Override
                            public BiomeHolder load(@NotNull Vector2 key) {
                                return pipeline.getBiomes(key.getBlockX(), key.getBlockZ());
                            }
                        }
                );
        this.pipeline = pipeline;
    }

    @Override
    public TerraBiome getBiome(int x, int z) {
        x += mutator.getNoiseSeeded(seed, x, z) * noiseAmp;
        z += mutator.getNoiseSeeded(1 + seed, x, z) * noiseAmp;


        x = FastMath.floorToInt(FastMath.floorDiv(x, resolution));

        z = FastMath.floorToInt(FastMath.floorDiv(z, resolution));

        int fdX = FastMath.floorDiv(x, pipeline.getSize());
        int fdZ = FastMath.floorDiv(z, pipeline.getSize());
        return holderCache.getUnchecked(new Vector2(fdX, fdZ)).getBiome(x - fdX * pipeline.getSize(), z - fdZ * pipeline.getSize());
    }

    public int getResolution() {
        return resolution;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    public interface ExceptionalFunction<I, O> {
        O apply(I in) throws ConfigException;
    }

    public static final class StandardBiomeProviderBuilder implements BiomeProviderBuilder {
        private final ExceptionalFunction<Long, BiomePipeline> pipelineBuilder;
        private final TerraPlugin main;
        private int resolution = 1;
        private double noiseAmp = 2;
        private NoiseSeeded builder;

        public StandardBiomeProviderBuilder(ExceptionalFunction<Long, BiomePipeline> pipelineBuilder, TerraPlugin main) {
            this.pipelineBuilder = pipelineBuilder;
            this.main = main;
        }

        public void setResolution(int resolution) {
            this.resolution = resolution;
        }

        public void setBlender(NoiseSeeded builder) {
            this.builder = builder;
        }

        public void setNoiseAmp(double noiseAmp) {
            this.noiseAmp = noiseAmp;
        }

        @Override
        public StandardBiomeProvider build(long seed) {
            try {
                StandardBiomeProvider provider = new StandardBiomeProvider(pipelineBuilder.apply(seed), main, builder.apply(seed), noiseAmp, (int) seed);
                provider.setResolution(resolution);
                return provider;
            } catch(ConfigException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
