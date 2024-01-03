package io.github.chaosdave34;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import gg.essential.api.EssentialAPI;
import io.github.chaosdave34.commands.SBHUDCommand;
import io.github.chaosdave34.listeners.NetworkListener;
import io.github.chaosdave34.listeners.PlayerListener;
import io.github.chaosdave34.listeners.RenderListener;
import io.github.chaosdave34.utils.Utils;
import io.github.chaosdave34.utils.gson.GsonInitializableTypeAdapter;
import io.github.chaosdave34.utils.gson.PatternAdapter;
import lombok.Getter;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Mod(modid = SBHUD.MODID, version = SBHUD.VERSION)
public class SBHUD {
    public static final String MODID = "sbhud";
    public static final String VERSION = "0.1";
    public static final String MOD_NAME = "SBHUD";

    @Getter private static SBHUD instance;
    @Getter private static final Logger LOGGER = LogManager.getLogger();
    @Getter private static File configDir;

    @Getter private final Utils utils;
    @Getter private final RenderListener renderListener;
    @Getter private final PlayerListener playerListener;

    @Getter
    private final Set<Integer> registeredFeatureIDs = new HashSet<>();

    @Getter private final Config config;
    @Getter private ComponentsGui componentsGui;

    @Getter
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(EnumMap.class, (InstanceCreator<EnumMap>) type -> {
                Type[] types = (((ParameterizedType) type).getActualTypeArguments());
                return new EnumMap((Class<?>) types[0]);
            })
            .registerTypeAdapterFactory(new GsonInitializableTypeAdapter())
            .registerTypeAdapter(Pattern.class, new PatternAdapter())
            .create();

    public SBHUD() {
        instance = this;

        utils = new Utils();
        renderListener = new RenderListener();
        playerListener = new PlayerListener();

        config = Config.INSTANCE;
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(renderListener);
        MinecraftForge.EVENT_BUS.register(playerListener);
        MinecraftForge.EVENT_BUS.register(new NetworkListener());

        EssentialAPI.getCommandRegistry().registerCommand(new SBHUDCommand());

        componentsGui = new ComponentsGui();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        configDir = event.getModConfigurationDirectory();
    }

}