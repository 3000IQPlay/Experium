package dev._3000IQPlay.experium.features.modules.player;

import dev._3000IQPlay.experium.event.events.PacketEvent;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.util.MathUtil;
import dev._3000IQPlay.experium.util.Timer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Blink
        extends Module {
    private static Blink INSTANCE = new Blink();
    private final Timer timer = new Timer();
    private final Queue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
    public Setting<Boolean> cPacketPlayer = this.register(new Setting<>("CPacketPlayer", true));
    public Setting<Mode> autoOff = this.register(new Setting<>("AutoOff", Mode.MANUAL));
    public Setting<Integer> timeLimit = this.register(new Setting<Object>("Time", 20, 1, 500, v -> this.autoOff.getValue() == Mode.TIME));
    public Setting<Integer> packetLimit = this.register(new Setting<Object>("Packets", 20, 1, 500, v -> this.autoOff.getValue() == Mode.PACKETS));
    public Setting<Float> distance = this.register(new Setting<Object>("Distance", 10.0f, 1.0f, 100.0f, v -> this.autoOff.getValue() == Mode.DISTANCE));
    private EntityOtherPlayerMP entity;
    private int packetsCanceled;
    private BlockPos startPos;

    public Blink() {
        super("Blink", "Fakelag.", Module.Category.PLAYER, true, false, false);
        this.setInstance();
    }

    public static Blink getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Blink();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        if (!Blink.fullNullCheck()) {
            this.entity = new EntityOtherPlayerMP(Blink.mc.world, Blink.mc.session.getProfile());
            this.entity.copyLocationAndAnglesFrom(Blink.mc.player);
            this.entity.rotationYaw = Blink.mc.player.rotationYaw;
            this.entity.rotationYawHead = Blink.mc.player.rotationYawHead;
            this.entity.inventory.copyInventory(Blink.mc.player.inventory);
            Blink.mc.world.addEntityToWorld(6942069, this.entity);
            this.startPos = Blink.mc.player.getPosition();
        } else {
            this.disable();
        }
        this.packetsCanceled = 0;
        this.timer.reset();
    }

    @Override
    public void onUpdate() {
        if (Blink.nullCheck() || this.autoOff.getValue() == Mode.TIME && this.timer.passedS(this.timeLimit.getValue()) || this.autoOff.getValue() == Mode.DISTANCE && this.startPos != null && Blink.mc.player.getDistanceSq(this.startPos) >= MathUtil.square(this.distance.getValue()) || this.autoOff.getValue() == Mode.PACKETS && this.packetsCanceled >= this.packetLimit.getValue()) {
            this.disable();
        }
    }

    @Override
    public void onLogout() {
        if (this.isOn()) {
            this.disable();
        }
    }

    @SubscribeEvent
    public void onSendPacket(PacketEvent.Send event) {
        if (event.getStage() == 0 && Blink.mc.world != null && !mc.isSingleplayer()) {
            Object packet = event.getPacket();
            if (this.cPacketPlayer.getValue() && packet instanceof CPacketPlayer) {
                event.setCanceled(true);
                this.packets.add((Packet<?>) packet);
                ++this.packetsCanceled;
            }
            if (!this.cPacketPlayer.getValue()) {
                if (packet instanceof CPacketChatMessage || packet instanceof CPacketConfirmTeleport || packet instanceof CPacketKeepAlive || packet instanceof CPacketTabComplete || packet instanceof CPacketClientStatus) {
                    return;
                }
                this.packets.add((Packet<?>) packet);
                event.setCanceled(true);
                ++this.packetsCanceled;
            }
        }
    }

    @Override
    public void onDisable() {
        if (!Blink.fullNullCheck()) {
            Blink.mc.world.removeEntity(this.entity);
            while (!this.packets.isEmpty()) {
                Blink.mc.player.connection.sendPacket(this.packets.poll());
            }
        }
        this.startPos = null;
    }

    public
    enum Mode {
        MANUAL,
        TIME,
        DISTANCE,
        PACKETS

    }
}

