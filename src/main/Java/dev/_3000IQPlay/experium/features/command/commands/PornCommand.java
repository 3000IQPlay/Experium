package dev._3000IQPlay.experium.features.command.commands;

import dev._3000IQPlay.experium.features.command.Command;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

public class PornCommand
        extends Command {
    Desktop desktop = Desktop.getDesktop();

    public PornCommand() {
        super("porn", new String[]{"type"});
    }

    @Override
    public void execute(String[] commands) {
        try {
            desktop.browse(new URI("https://www.pornhub.com/video/search?search=" + URLEncoder.encode(commands[0])));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}