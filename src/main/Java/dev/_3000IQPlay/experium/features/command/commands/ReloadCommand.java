package dev._3000IQPlay.experium.features.command.commands;

import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.features.command.Command;

public class ReloadCommand
        extends Command {
    public ReloadCommand() {
        super("reload", new String[0]);
    }

    @Override
    public void execute(String[] commands) {
        Experium.reload();
    }
}

