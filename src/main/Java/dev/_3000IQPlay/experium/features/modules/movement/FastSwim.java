package dev._3000IQPlay.experium.features.modules.movement;

import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.event.events.MoveEvent;
import dev._3000IQPlay.experium.util.MovementUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FastSwim
        extends Module {
    public Setting<Double> wspeed = this.register(new Setting<Double>("WaterSpeed", 1.5, 1.0, 10.0));
    public Setting<Double> lspeed = this.register(new Setting<Double>("LavaSpeed", 1.5, 1.0, 10.0));
    public Setting<Boolean> water = this.register(new Setting<Boolean>("Water", true));
    public Setting<Boolean> lava = this.register(new Setting<Boolean>("Lava", true));
    public Setting<Boolean> antiKick = this.register(new Setting<Boolean>("AntiKick", false));

    public FastSwim() {
        super("FastSwim", "Swim fast", Module.Category.MOVEMENT, true, false, false);
    }

    @SubscribeEvent
    public void onMove(MoveEvent moveEvent) {
        if (!MovementUtil.isMoving((EntityLivingBase)FastSwim.mc.player)) {
            return;
        }
        if (FastSwim.mc.player.isInLava() && !FastSwim.mc.player.onGround && this.lava.getValue().booleanValue()) {
            double[] arrd = FastSwim.mc.player.ticksExisted % 4 == 0 && this.antiKick.getValue() != false ? MovementUtil.forward(this.lspeed.getValue() / 40.0) : MovementUtil.forward(this.lspeed.getValue() / 10.0);
            moveEvent.setX(arrd[0]);
            moveEvent.setZ(arrd[1]);
        } else if (FastSwim.mc.player.isInWater() && !FastSwim.mc.player.onGround && this.water.getValue().booleanValue()) {
            double[] arrd = FastSwim.mc.player.ticksExisted % 4 == 0 && this.antiKick.getValue() != false ? MovementUtil.forward(this.wspeed.getValue() / 40.0) : MovementUtil.forward(this.wspeed.getValue() / 10.0);
            moveEvent.setX(arrd[0]);
            moveEvent.setZ(arrd[1]);
        }
    }
}