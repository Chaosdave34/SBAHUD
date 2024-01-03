package io.github.chaosdave34.events;

import io.github.chaosdave34.utils.Utils;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This is fired by {@link Utils#parseSidebar()} when the player joins Hypixel Skyblock.
 */
public class SkyblockJoinedEvent extends Event {
    // This is intentionally empty since there's no useful data we need to include.
}