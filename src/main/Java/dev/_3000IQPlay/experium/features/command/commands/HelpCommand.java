package dev._3000IQPlay.experium.features.command.commands;

import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.features.command.Command;

public class HelpCommand
        extends Command {
    public HelpCommand() {
        super("help");
    }

    @Override
    public void execute(String[] commands) {
        HelpCommand.sendMessage("You can use following commands: ");
        for (Command command : Experium.commandManager.getCommands()) {
            HelpCommand.sendMessage(Experium.commandManager.getPrefix() + command.getName());
        }
    }
}