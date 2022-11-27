package dev._3000IQPlay.experium.features.modules.player;

import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Bind;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.util.InventoryUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class SilentXP
        extends Module {
    public Setting<Mode> mode = this.register(new Setting<>("Mode", Mode.MIDDLECLICK));
    public Setting<Boolean> antiFriend = this.register(new Setting<>("AntiFriend", true));
    public Setting<Bind> key = this.register(new Setting<>("Key", new Bind(-1), v -> mode.getValue() != Mode.MIDDLECLICK));
    public Setting<Boolean> groundOnly = this.register(new Setting<>("BelowHorizon", false));
    private boolean last;
    private boolean on;

    public SilentXP() {
        super("SilentXP", "Silent XP", Module.Category.PLAYER, false, false, false);
    }

    @Override
    public void onUpdate() {
        if (SilentXP.fullNullCheck()) return;
        switch (this.mode.getValue()) {
            case PRESS:
                if (this.key.getValue().isDown())
                    this.throwXP(false);
                break;
            case TOGGLE:
                if (toggled()) {
                    this.throwXP(false);
                }
                break;
            default:
                if (this.groundOnly.getValue() && SilentXP.mc.player.rotationPitch < 0) return;
                if (Mouse.isButtonDown(2))
                    this.throwXP(true);
        }
    }

    private boolean toggled() {
        if (this.key.getValue().getKey() == -1)
            return false;
        if (!Keyboard.isKeyDown(this.key.getValue().getKey())) {
            this.last = true;
        } else if ((Keyboard.isKeyDown(this.key.getValue().getKey())) && this.last && !this.on) {
            this.last = false;
            this.on = true;
            return this.on;
        } else if ((Keyboard.isKeyDown(this.key.getValue().getKey())) && this.last && this.on) {
            this.last = false;
            this.on = false;
            return this.on;
        }
        return this.on;
    }

    private void throwXP(boolean mcf) {
        boolean offhand;
        RayTraceResult result;
        if (mcf && this.antiFriend.getValue() && (result = SilentXP.mc.objectMouseOver) != null && result.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit instanceof EntityPlayer)
            return;
        int xpSlot = InventoryUtil.findHotbarBlock(ItemExpBottle.class);
        offhand = SilentXP.mc.player.getHeldItemOffhand().getItem() == Items.EXPERIENCE_BOTTLE;
        if (xpSlot != -1 || offhand) {
            int oldslot = SilentXP.mc.player.inventory.currentItem;
            if (!offhand) {
                InventoryUtil.switchToHotbarSlot(xpSlot, false);
            }
            SilentXP.mc.playerController.processRightClick(SilentXP.mc.player, SilentXP.mc.world, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
            if (!offhand) {
                InventoryUtil.switchToHotbarSlot(oldslot, false);
            }
        }
    }

    public enum Mode {
        MIDDLECLICK,
        TOGGLE,
        PRESS
    }
}
