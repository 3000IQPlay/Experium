package dev._3000IQPlay.experium.features.command.commands;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import dev._3000IQPlay.experium.features.command.Command;

public class OpenFolderCommand
        extends Command {
    public OpenFolderCommand() {
        super("openfolder", new String[0]);
    }

    @Override
    public void execute(String[] commands) {
        try {
            Desktop.getDesktop().open(new File("experium/"));
            Command.sendMessage("Opened config folder!", false);
        }
        catch (IOException e) {
            Command.sendMessage("Could not open config folder!", false);
            e.printStackTrace();
        }
    }
}