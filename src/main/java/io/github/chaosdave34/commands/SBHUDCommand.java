package io.github.chaosdave34.commands;

import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;
import gg.essential.api.commands.Options;
import gg.essential.api.commands.SubCommand;
import gg.essential.api.utils.GuiUtil;
import io.github.chaosdave34.SBHUD;

public class SBHUDCommand extends Command {
    public SBHUDCommand() {
        super("sbhud");
    }

    @DefaultHandler
    public void handle() {
        GuiUtil.open(SBHUD.config.gui());
    }

    @SubCommand(value = "gui")
    public void onGuiSubCommand() {
        GuiUtil.open(SBHUD.INSTANCE.getComponentsGui());
    }

}
