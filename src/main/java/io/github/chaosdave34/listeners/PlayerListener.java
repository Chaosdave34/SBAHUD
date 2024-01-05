package io.github.chaosdave34.listeners;


import io.github.chaosdave34.SBHUD;
import io.github.chaosdave34.core.Attribute;
import io.github.chaosdave34.utils.ActionBarParser;
import io.github.chaosdave34.utils.TabListParser;
import io.github.chaosdave34.utils.ScoreboardManager;
import io.github.chaosdave34.utils.SidebarParser;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Iterator;
import java.util.regex.Pattern;

//TODO Fix for Hypixel localization
public class PlayerListener {


    private int timerTick = 1;


    private final SBHUD main = SBHUD.INSTANCE;
    @Getter
    private final ActionBarParser actionBarParser = new ActionBarParser();
    @Getter
    private final SidebarParser scoreboardParser = new SidebarParser();
    @Getter
    private final TabListParser tabListParser = new TabListParser();


    /**
     * Reset all the timers and stuff when joining a new world.
     */
    @SubscribeEvent()
    public void onWorldJoin(EntityJoinWorldEvent e) {
        Entity entity = e.entity;
        if (entity == Minecraft.getMinecraft().thePlayer) {
            timerTick = 1;
        }
    }

    /**
     * Interprets the action bar to extract mana, health, and defence. Enables/disables mana/health prediction,
     * and looks for mana usage messages in chat while predicting.
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onChatReceive(ClientChatReceivedEvent e) {
        if (!main.getUtils().isOnHypixel()) {
            return;
        }

        String formattedText = e.message.getFormattedText();
        String unformattedText = e.message.getUnformattedText();

        if (formattedText.startsWith("§7Sending to server ")) {
            return;
        }

        if (main.getUtils().isOnSkyblock()) {
            // Type 2 means it's an action bar message.
            if (e.type == 2) {
                // Parse using ActionBarParser and display the rest message instead
                actionBarParser.parseActionBar(unformattedText);

            } else {
                if (main.getRenderListener().isPredictMana() && unformattedText.startsWith("Used ") && unformattedText.endsWith("Mana)")) {
                    int manaLost = Integer.parseInt(unformattedText.split(Pattern.quote("! ("))[1].split(Pattern.quote(" Mana)"))[0]);
                    changeMana(-manaLost);
                }
            }
        }
    }

    /**
     * Acts as a callback to set the actionbar message after other mods have a chance to look at the message
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onChatReceiveLast(ClientChatReceivedEvent e) {
        if (e.type == 2 && !e.isCanceled()) {
            Iterator<String> itr = actionBarParser.getStringsToRemove().iterator();
            String message = e.message.getUnformattedText();
            while (itr.hasNext()) {
                String next = itr.next();
                message = message.replaceAll(" *" + Pattern.quote(next), "");
            }
            message = message.trim();
            e.message = new ChatComponentText(message);
        }
    }


    /**
     * The main timer for a bunch of stuff.
     */
    @SubscribeEvent()
    public void onTick(TickEvent.ClientTickEvent e) {
        if (e.phase == TickEvent.Phase.START) {
            Minecraft mc = Minecraft.getMinecraft();
            timerTick++;

            if (mc != null) { // Predict health every tick if needed.
                ScoreboardManager.tick();

                if (actionBarParser.getHealthUpdate() != null && System.currentTimeMillis() - actionBarParser.getLastHealthUpdate() > 3000) {
                    actionBarParser.setHealthUpdate(null);
                }
                EntityPlayerSP p = mc.thePlayer;
                if (p != null && SBHUD.config.healthPrediction) { //Reverse calculate the player's health by using the player's vanilla hearts. Also calculate the health change for the gui item.
                    float newHealth = getAttribute(Attribute.HEALTH) > getAttribute(Attribute.MAX_HEALTH) ?
                            getAttribute(Attribute.HEALTH) : Math.round(getAttribute(Attribute.MAX_HEALTH) * ((p.getHealth()) / p.getMaxHealth()));
                    setAttribute(Attribute.HEALTH, newHealth);
                }

                if (timerTick == 20) {
                    // Add natural mana every second (increase is based on your max mana).
                    if (main.getRenderListener().isPredictMana()) {
                        // If regen-ing, cap at the max mana
                        if (getAttribute(Attribute.MANA) < getAttribute(Attribute.MAX_MANA)) {
                            setAttribute(Attribute.MANA, Math.min(getAttribute(Attribute.MANA) + getAttribute(Attribute.MAX_MANA) / 50, getAttribute(Attribute.MAX_MANA)));
                        }
                        // If above mana cap, do nothing
                    }
                } else if (timerTick % 5 == 0) { // Check inventory, location, updates, and skeleton helmet every 1/4 second.
                    EntityPlayerSP player = mc.thePlayer;

                    if (player != null) {
                        scoreboardParser.parseSidebar();
                        tabListParser.parseTabList();
                    }
                } else if (timerTick > 20) { // To keep the timer going from 1 to 21 only.
                    timerTick = 1;
                }
            }
        }
    }

    private void changeMana(float change) {
        setAttribute(Attribute.MANA, getAttribute(Attribute.MANA) + change);
    }

    public String getTickerText() {
        return actionBarParser.getTickerText();
    }

    public boolean isAligned() {
        return actionBarParser.isAligned();
    }

    public String getSalvationText() {
        return actionBarParser.getSalvationText();
    }

    public float getTrueDefence() {
        return actionBarParser.getTrueDefence();
    }

    public String getHealingWandText() {
        return actionBarParser.getHealingWandText();
    }

    private float getAttribute(Attribute attribute) {
        return main.getUtils().getAttributes().get(attribute).getValue();
    }

    private void setAttribute(Attribute attribute, float value) {
        main.getUtils().getAttributes().get(attribute).setValue(value);
    }


}
