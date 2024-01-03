package io.github.chaosdave34.listeners;


import io.github.chaosdave34.SBHUD;
import io.github.chaosdave34.events.SkyblockJoinedEvent;
import io.github.chaosdave34.events.SkyblockLeftEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.logging.log4j.Logger;

import static net.minecraftforge.common.MinecraftForge.EVENT_BUS;

public class NetworkListener {

    private static final Logger logger = SBHUD.logger;

    private final SBHUD main;

    public NetworkListener() {
        main = SBHUD.INSTANCE;
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        // Leave Skyblock when the player disconnects
        EVENT_BUS.post(new SkyblockLeftEvent());
    }

    @SubscribeEvent
    public void onSkyblockJoined(SkyblockJoinedEvent event) {
        logger.info("Detected joining skyblock!");
        main.getUtils().setOnSkyblock(true);
    }

    @SubscribeEvent
    public void onSkyblockLeft(SkyblockLeftEvent event) {
        logger.info("Detected leaving skyblock!");
        main.getUtils().setOnSkyblock(false);
    }
}