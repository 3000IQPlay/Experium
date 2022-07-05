package dev._3000IQPlay.experium.features.command.commands;

import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.features.command.Command;
import dev._3000IQPlay.experium.features.modules.client.ClickGui;

public class PrefixCommand
        extends Command {
    public PrefixCommand() {
        super("prefix", new String[]{"<char>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            Command.sendMessage("\u00a7cSpecify a new prefix.");
            return;
        }
        Experium.moduleManager.getModuleByClass(ClickGui.class).prefix.setValue(commands[0]);
        Command.sendMessage("Prefix set to \u00a7a" + Experium.commandManager.getPrefix());
    }
}

