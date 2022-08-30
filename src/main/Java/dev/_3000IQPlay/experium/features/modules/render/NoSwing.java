package dev._3000IQPlay.experium.features.modules.render;

import dev._3000IQPlay.experium.event.events.PacketEvent;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoSwing
        extends Module {
    public Setting<Switch> switchSetting = this.register(new Setting<Switch>("Switch", Switch.ONEDOTEIGHT));
    public Setting<Swing> swing = this.register(new Setting<Swing>("Swing", Swing.MAINHAND));

    public NoSwing() {
        super("NoSwing", "Shopgirls requested this soooo", Module.Category.PLAYER, true, false, false);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketAnimation) {
            if (this.swing.getValue() == Swing.MAINHAND) {
                NoSwing.mc.player.swingingHand = EnumHand.MAIN_HAND;
            }
            if (this.swing.getValue() == Swing.OFFHAND) {
                NoSwing.mc.player.swingingHand = EnumHand.OFF_HAND;
            }
            if (this.swing.getValue() == Swing.CANCEL) {
                event.setCanceled(true);
            }
        }
    }

    @Override
    public void onTick() {
        if (NoSwing.fullNullCheck()) {
            return;
        }
        if (this.switchSetting.getValue() == Switch.ONEDOTEIGHT && (double)NoSwing.mc.entityRenderer.itemRenderer.prevEquippedProgressMainHand >= 0.9) {
            NoSwing.mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1.0f;
            NoSwing.mc.entityRenderer.itemRenderer.itemStackMainHand = NoSwing.mc.player.getHeldItemMainhand();
        }
    }

    public static enum Swing {
        MAINHAND,
        OFFHAND,
        CANCEL;
    }

    public static enum Switch {
        ONEDOTEIGHT,
        ONEDOTNINE;
    }
}
