package dev._3000IQPlay.experium.features.modules.misc;

import dev._3000IQPlay.experium.event.events.DeathEvent;
import dev._3000IQPlay.experium.features.setting.*;
import dev._3000IQPlay.experium.features.modules.Module;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class AutoKit extends Module {
    public AutoKit() {
        super("AutoKit", "Automatically does /kit <name>", Module.Category.MISC, true, false, false);
        this.setInstance();
    }
    public Setting<String> kit = this.register(new Setting("Kit", ""));
    private boolean toggle = false;
    private boolean runThisLife = false;
    public static AutoKit INSTANCE;

    public static AutoKit getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AutoKit();
        }
        return INSTANCE;
    }

    public void onUpdate() {
        if (mc.player.isEntityAlive() && !this.runThisLife) {
            mc.player.sendChatMessage("/kit " + ((String)this.kit.getValue()));
            this.runThisLife = true;
        }
    }

    @SubscribeEvent
    public void onEntityDeath(DeathEvent event) {
        if (event.player == mc.player) {
            this.runThisLife = false;
        }
    }

    private void setInstance() {
        INSTANCE = this;
    }
}
