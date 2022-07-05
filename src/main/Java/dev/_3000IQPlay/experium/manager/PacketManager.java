package dev._3000IQPlay.experium.manager;

import dev._3000IQPlay.experium.features.Feature;
import dev._3000IQPlay.experium.event.events.PacketEvent;
import dev._3000IQPlay.experium.util.Timer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

public class PacketManager
        extends Feature {
    private final List<Packet<?>> noEventPackets = new ArrayList();
	SPacketExplosion pExplosion = null;
    Timer timerExplosion = new Timer();
    boolean caughtPExplosion = false;
    SPacketPlayerPosLook pPlayerPosLook = null;
    Timer timerPlayerPosLook = new Timer();
    boolean caughtPlayerPosLook = false;

    public void sendPacketNoEvent(Packet<?> packet) {
        if (packet != null && !PacketManager.nullCheck()) {
            this.noEventPackets.add(packet);
            PacketManager.mc.player.connection.sendPacket(packet);
        }
    }

    public boolean shouldSendPacket(Packet<?> packet) {
        if (this.noEventPackets.contains(packet)) {
            this.noEventPackets.remove(packet);
            return false;
        }
        return true;
    }
	
	@SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e) {
        if (PacketManager.fullNullCheck()) {
            return;
        }
        if (e.getPacket() instanceof SPacketPlayerPosLook) {
            this.pPlayerPosLook = (SPacketPlayerPosLook)e.getPacket();
            this.timerPlayerPosLook.reset();
            this.caughtPlayerPosLook = true;
        }
        if (e.getPacket() instanceof SPacketExplosion) {
            this.pExplosion = (SPacketExplosion)e.getPacket();
            this.timerExplosion.reset();
            this.caughtPExplosion = true;
        }
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent e) {
        if (PacketManager.fullNullCheck()) {
            return;
        }
        if (this.timerExplosion.passedMs(250L)) {
            this.pExplosion = null;
            this.caughtPExplosion = false;
        }
        if (this.timerPlayerPosLook.passedMs(250L)) {
            this.pPlayerPosLook = null;
            this.caughtPlayerPosLook = false;
        }
    }

    public boolean caughtPlayerPosLook() {
        return this.caughtPlayerPosLook;
    }

    public boolean caughtPExplosion() {
        return this.caughtPExplosion;
    }

    public SPacketPlayerPosLook pPlayerPosLook() {
        return this.pPlayerPosLook;
    }

    public SPacketExplosion pExplosion() {
        return this.pExplosion;
    }

    public Timer timerExplosion() {
        return this.timerExplosion;
    }

    public Timer timerPlayerPosLook() {
        return this.timerPlayerPosLook;
    }
}

