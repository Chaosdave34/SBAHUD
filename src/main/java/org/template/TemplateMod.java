package org.template;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = TemplateMod.MODID, version = TemplateMod.VERSION)
public class TemplateMod {
    public static final String MODID = "templatemod";
    public static final String VERSION = "1.0";

    @EventHandler
    public void init(FMLInitializationEvent event) {
        System.out.println("TEMPLATE MOD");
    }
}