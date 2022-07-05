package dev._3000IQPlay.experium.features.command.commands;

import dev._3000IQPlay.experium.features.command.Command;

public class ClearRamCommand
        extends Command {
    public ClearRamCommand() {
        super("clearram");
    }

    @Override
    public void execute(String[] commands) {
        System.gc();
        Command.sendMessage("Finished clearing the ram.", false);
    }
}