package com.dfsek.terra.config.pack;

import com.dfsek.tectonic.abstraction.AbstractConfigLoader;
import com.dfsek.tectonic.exception.ConfigException;
import com.dfsek.tectonic.exception.LoadException;
import com.dfsek.tectonic.loading.ConfigLoader;
import com.dfsek.tectonic.loading.TypeRegistry;
import com.dfsek.terra.api.LoaderRegistrar;
import com.dfsek.terra.api.core.TerraPlugin;
import com.dfsek.terra.api.structures.loot.LootTable;
import com.dfsek.terra.api.structures.script.StructureScript;
import com.dfsek.terra.api.world.flora.Flora;
import com.dfsek.terra.api.world.palette.Palette;
import com.dfsek.terra.api.world.tree.Tree;
import com.dfsek.terra.biome.TerraBiome;
import com.dfsek.terra.biome.provider.BiomeProvider;
import com.dfsek.terra.carving.UserDefinedCarver;
import com.dfsek.terra.config.exception.FileMissingException;
import com.dfsek.terra.config.factories.BiomeFactory;
import com.dfsek.terra.config.factories.CarverFactory;
import com.dfsek.terra.config.factories.FloraFactory;
import com.dfsek.terra.config.factories.OreFactory;
import com.dfsek.terra.config.factories.PaletteFactory;
import com.dfsek.terra.config.factories.StructureFactory;
import com.dfsek.terra.config.factories.TerraFactory;
import com.dfsek.terra.config.factories.TreeFactory;
import com.dfsek.terra.config.fileloaders.FolderLoader;
import com.dfsek.terra.config.fileloaders.Loader;
import com.dfsek.terra.config.fileloaders.ZIPLoader;
import com.dfsek.terra.config.lang.LangUtil;
import com.dfsek.terra.config.loaders.config.BufferedImageLoader;
import com.dfsek.terra.config.loaders.config.biome.BiomeProviderBuilderLoader;
import com.dfsek.terra.config.templates.AbstractableTemplate;
import com.dfsek.terra.config.templates.BiomeTemplate;
import com.dfsek.terra.config.templates.CarverTemplate;
import com.dfsek.terra.config.templates.FloraTemplate;
import com.dfsek.terra.config.templates.OreTemplate;
import com.dfsek.terra.config.templates.PaletteTemplate;
import com.dfsek.terra.config.templates.StructureTemplate;
import com.dfsek.terra.config.templates.TreeTemplate;
import com.dfsek.terra.registry.BiomeRegistry;
import com.dfsek.terra.registry.CarverRegistry;
import com.dfsek.terra.registry.FloraRegistry;
import com.dfsek.terra.registry.LootRegistry;
import com.dfsek.terra.registry.OreRegistry;
import com.dfsek.terra.registry.PaletteRegistry;
import com.dfsek.terra.registry.ScriptRegistry;
import com.dfsek.terra.registry.StructureRegistry;
import com.dfsek.terra.registry.TerraRegistry;
import com.dfsek.terra.registry.TreeRegistry;
import com.dfsek.terra.world.generation.math.SamplerCache;
import com.dfsek.terra.world.population.items.TerraStructure;
import com.dfsek.terra.world.population.items.ores.Ore;
import org.apache.commons.io.IOUtils;
import org.json.simple.parser.ParseException;
import parsii.eval.Scope;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Represents a Terra configuration pack.
 */
public class ConfigPack implements LoaderRegistrar {
    private final ConfigPackTemplate template = new ConfigPackTemplate();

    private final BiomeRegistry biomeRegistry = new BiomeRegistry();
    private final StructureRegistry structureRegistry = new StructureRegistry();
    private final PaletteRegistry paletteRegistry;
    private final FloraRegistry floraRegistry;
    private final OreRegistry oreRegistry = new OreRegistry();
    private final TreeRegistry treeRegistry;
    private final ScriptRegistry scriptRegistry = new ScriptRegistry();
    private final LootRegistry lootRegistry = new LootRegistry();

    private final CarverRegistry carverRegistry = new CarverRegistry();

    private final AbstractConfigLoader abstractConfigLoader = new AbstractConfigLoader();
    private final ConfigLoader selfLoader = new ConfigLoader();
    private final Scope varScope = new Scope();

    private final SamplerCache samplerCache;

    private final TerraPlugin main;
    private final Loader loader;


    public ConfigPack(File folder, TerraPlugin main) throws ConfigException {
        this.loader = new FolderLoader(folder.toPath());
        this.main = main;
        long l = System.nanoTime();
        this.samplerCache = new SamplerCache(main);
        floraRegistry = new FloraRegistry(main);
        paletteRegistry = new PaletteRegistry(main);
        treeRegistry = new TreeRegistry(main);
        register(abstractConfigLoader);
        register(selfLoader);

        main.register(selfLoader);
        main.register(abstractConfigLoader);

        File pack = new File(folder, "pack.yml");

        try {
            selfLoader.load(template, new FileInputStream(pack));
        } catch(FileNotFoundException e) {
            throw new FileMissingException("No pack.yml file found in " + folder.getAbsolutePath(), e);
        }
        load(l, main);
    }

    public ConfigPack(ZipFile file, TerraPlugin main) throws ConfigException {
        this.loader = new ZIPLoader(file);
        this.main = main;
        long l = System.nanoTime();
        this.samplerCache = new SamplerCache(main);
        floraRegistry = new FloraRegistry(main);
        paletteRegistry = new PaletteRegistry(main);
        treeRegistry = new TreeRegistry(main);
        register(abstractConfigLoader);
        register(selfLoader);

        main.register(selfLoader);
        main.register(abstractConfigLoader);

        InputStream stream = null;

        try {
            Enumeration<? extends ZipEntry> entries = file.entries();
            while(entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if(entry.getName().equals("pack.yml")) stream = file.getInputStream(entry);
            }
        } catch(IOException e) {
            throw new LoadException("Unable to load pack.yml from ZIP file", e);
        }
        if(stream == null) throw new FileMissingException("No pack.yml file found in " + file.getName());

        selfLoader.load(template, stream);


        load(l, main);

        template.getProviderBuilder().build(0); // Build dummy provider to catch errors at load time.
    }

    private void load(long start, TerraPlugin main) throws ConfigException {
        main.packPreLoadCallback(this);
        for(Map.Entry<String, Double> var : template.getVariables().entrySet()) {
            varScope.create(var.getKey()).setValue(var.getValue());
        }
        loader.open("structures/data", ".tstructure")
                .thenNames(ids -> ids.forEach(id -> main.getLogger().severe("Found .tstructure file \"" + id + "\". This file must be updated to the newer .tesf format to work properly. See version 3.0 release notes for instructions.")))
                .close();

        loader.open("structures/data", ".tesf").thenEntries(entries -> {
            for(Map.Entry<String, InputStream> entry : entries) {
                try {
                    StructureScript structureScript = new StructureScript(entry.getValue(), main, scriptRegistry, lootRegistry, samplerCache);
                    scriptRegistry.add(structureScript.getId(), structureScript);
                } catch(com.dfsek.terra.api.structures.parser.exceptions.ParseException e) {
                    throw new LoadException("Unable to load script \"" + entry.getKey() + "\"", e);
                }
            }
        }).close().open("structures/loot", ".json").thenEntries(entries -> {
            for(Map.Entry<String, InputStream> entry : entries) {
                try {
                    lootRegistry.add(entry.getKey(), new LootTable(IOUtils.toString(entry.getValue(), StandardCharsets.UTF_8), main));
                } catch(ParseException | IOException | NullPointerException e) {
                    throw new LoadException("Unable to load loot table \"" + entry.getKey() + "\"", e);
                }
            }
        }).close();

        loader
                .open("carving", ".yml").then(streams -> buildAll(new CarverFactory(this), carverRegistry, abstractConfigLoader.load(streams, CarverTemplate::new), main)).close()
                .open("palettes", ".yml").then(streams -> buildAll(new PaletteFactory(), paletteRegistry, abstractConfigLoader.load(streams, PaletteTemplate::new), main)).close()
                .open("ores", ".yml").then(streams -> buildAll(new OreFactory(), oreRegistry, abstractConfigLoader.load(streams, OreTemplate::new), main)).close()
                .open("structures/trees", ".yml").then(streams -> buildAll(new TreeFactory(), treeRegistry, abstractConfigLoader.load(streams, TreeTemplate::new), main)).close()
                .open("structures/structures", ".yml").then(streams -> buildAll(new StructureFactory(), structureRegistry, abstractConfigLoader.load(streams, StructureTemplate::new), main)).close()
                .open("flora", ".yml").then(streams -> buildAll(new FloraFactory(), floraRegistry, abstractConfigLoader.load(streams, FloraTemplate::new), main)).close()
                .open("biomes", ".yml").then(streams -> buildAll(new BiomeFactory(this), biomeRegistry, abstractConfigLoader.load(streams, () -> new BiomeTemplate(this, main)), main)).close();
        main.packPostLoadCallback(this);
        LangUtil.log("config-pack.loaded", Level.INFO, template.getID(), String.valueOf((System.nanoTime() - start) / 1000000D), template.getAuthor(), template.getVersion());
    }

    public static <C extends AbstractableTemplate, O> void buildAll(TerraFactory<C, O> factory, TerraRegistry<O> registry, List<C> configTemplates, TerraPlugin main) throws LoadException {
        for(C template : configTemplates) registry.add(template.getID(), factory.build(template, main));
    }

    public TerraBiome getBiome(String id) {
        return biomeRegistry.get(id);
    }

    public List<String> getBiomeIDs() {
        return biomeRegistry.entries().stream().map(TerraBiome::getID).collect(Collectors.toList());
    }

    public TerraStructure getStructure(String id) {
        return structureRegistry.get(id);
    }

    public Set<TerraStructure> getStructures() {
        return structureRegistry.entries();
    }

    public List<String> getStructureIDs() {
        return structureRegistry.entries().stream().map(terraStructure -> terraStructure.getTemplate().getID()).collect(Collectors.toList());
    }

    public TreeRegistry getTreeRegistry() {
        return treeRegistry;
    }

    public ConfigPackTemplate getTemplate() {
        return template;
    }

    public Scope getVarScope() {
        return varScope;
    }


    @Override
    public void register(TypeRegistry registry) {
        registry
                .registerLoader(Palette.class, paletteRegistry)
                .registerLoader(TerraBiome.class, biomeRegistry)
                .registerLoader(Flora.class, floraRegistry)
                .registerLoader(Ore.class, oreRegistry)
                .registerLoader(Tree.class, treeRegistry)
                .registerLoader(StructureScript.class, scriptRegistry)
                .registerLoader(TerraStructure.class, structureRegistry)
                .registerLoader(LootTable.class, lootRegistry)
                .registerLoader(UserDefinedCarver.class, carverRegistry)
                .registerLoader(BufferedImage.class, new BufferedImageLoader(loader))
                .registerLoader(BiomeProvider.BiomeProviderBuilder.class, new BiomeProviderBuilderLoader(main, biomeRegistry, loader));
    }

    public ScriptRegistry getScriptRegistry() {
        return scriptRegistry;
    }

    public BiomeRegistry getBiomeRegistry() {
        return biomeRegistry;
    }

    public SamplerCache getSamplerCache() {
        return samplerCache;
    }

    public Set<UserDefinedCarver> getCarvers() {
        return carverRegistry.entries();
    }
}
