package io.github.chaosdave34;

import gg.essential.api.EssentialAPI;
import io.github.chaosdave34.commands.SBHUDCommand;
import io.github.chaosdave34.gui.ComponentsGui;
import io.github.chaosdave34.listeners.NetworkListener;
import io.github.chaosdave34.listeners.PlayerListener;
import io.github.chaosdave34.listeners.RenderListener;
import io.github.chaosdave34.utils.Utils;
import lombok.Getter;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = SBHUD.MODID, name = SBHUD.MODID, version = SBHUD.VERSION)
public class SBHUD {
    public static final String MODID = "@MODID@";
    public static final String MOD_NAME = "@MODNAME@";
    public static final String VERSION = "@VERSION@";

    @Mod.Instance(MODID)
    public static SBHUD INSTANCE;
    public static final Logger logger = LogManager.getLogger();
    public static final Config config = Config.INSTANCE;

    @Getter private static File configDir;

    @Getter private Utils utils;
    @Getter private RenderListener renderListener;
    @Getter private PlayerListener playerListener;

    @Getter private ComponentsGui componentsGui;

    @EventHandler
    public void init(FMLInitializationEvent event) {
        utils = new Utils();
        renderListener = new RenderListener();
        playerListener = new PlayerListener();

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