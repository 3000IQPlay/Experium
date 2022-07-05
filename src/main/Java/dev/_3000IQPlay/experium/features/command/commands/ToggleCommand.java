package dev._3000IQPlay.experium.features.command.commands;

import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.features.command.Command;
import dev._3000IQPlay.experium.features.modules.Module;

public class ToggleCommand
        extends Command {
    public ToggleCommand() {
        super("toggle", new String[]{"<toggle>", "<module>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 2) {
            String name = commands[0].replaceAll("_", " ");
            Module module = Experium.moduleManager.getModuleByName(name);
            if (module != null) {
                module.toggle();
            } else {
                Command.sendMessage("Unable to find a module with that name!");
            }
        } else {
            Command.sendMessage("Please provide a valid module name!");
        }
    }
}