package dev._3000IQPlay.experium.features.command.commands;

import dev._3000IQPlay.experium.features.command.Command;
import dev._3000IQPlay.experium.features.modules.player.FakePlayer;

public class FakePlayerCommand extends Command {

    public FakePlayerCommand() { super("fakeplayer"); }

    @Override
    public void execute(String[] commands) {
        FakePlayer.getInstance().enable();
    }
}