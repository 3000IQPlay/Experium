package dev._3000IQPlay.experium.features.modules.movement;

import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;

/**
 * @author Cubic
 * @since 24.11.2022
 */
public class ReverseStep extends Module {

    private final Setting<Double> height = register(new Setting<>("Height", 4, 0.1, 10));
    private final Setting<Double> speed = register(new Setting<>("Speed", 10, 1, 10));

    public ReverseStep(){
        super("ReverseStep", "", Category.MOVEMENT, false, false, false);
    }

    @Override
    public void onUpdate() {
        if(mc.player.isInWater() || mc.player.isInLava() || mc.player.isOnLadder() || mc.gameSettings.keyBindJump.isKeyDown())
            return;

        for(double y = 0.0; y < height.getValue() + 0.5; y += 0.01){
            if(mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0, -y, 0)).isEmpty())
                continue;
            mc.player.motionY = -speed.getValue();
            break;
        }
    }
}
