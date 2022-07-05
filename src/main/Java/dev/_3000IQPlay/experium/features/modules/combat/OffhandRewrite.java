package dev._3000IQPlay.experium.features.modules.combat;

import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.event.events.PacketEvent;
import dev._3000IQPlay.experium.event.events.ProcessRightClickBlockEvent;
import dev._3000IQPlay.experium.features.Feature;
import dev._3000IQPlay.experium.features.gui.ExperiumGui;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.modules.client.Notifications;
import dev._3000IQPlay.experium.features.modules.client.PingBypass;
import dev._3000IQPlay.experium.features.setting.Bind;
import dev._3000IQPlay.experium.features.setting.EnumConverter;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.util.DamageUtil;
import dev._3000IQPlay.experium.util.EntityUtil;
import dev._3000IQPlay.experium.util.InventoryUtil;
import dev._3000IQPlay.experium.util.Timer;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockWeb;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class OffhandRewrite
        extends Module {
    private static OffhandRewrite instance;
    private final Queue<InventoryUtil.Task> taskList;
    private final Timer timer;
    private final Timer secondTimer;
    private final Timer thirdtimer;
    public Mode2 currentMode;
    public int switchval;
    public int totems;
    public int crystals;
    public int gapples;
    public int lastTotemSlot;
    public int lastGappleSlot;
    public int lastCrystalSlot;
    public int lastObbySlot;
    public int lastWebSlot;
    public boolean holdingCrystal;
    public boolean holdingTotem;
    public boolean holdingGapple;
    public boolean didSwitchThisTick;
    public Setting<page> pageSetting = this.register(new Setting<page>("Page", page.MAIN));
    public Setting<Mode2> offhandmode = this.register(new Setting<Object>("Offhand", (Object)Mode2.TOTEMS, v -> this.pageSetting.getValue() == page.MAIN));
    public Setting<Boolean> rightGap = this.register(new Setting<Object>("Right Click Gap", Boolean.valueOf(true), v -> this.pageSetting.getValue() == page.MAIN));
    public Setting<Boolean> swordgap = this.register(new Setting<Object>("Sword Gap", Boolean.valueOf(false), v -> this.pageSetting.getValue() == page.MAIN));
    public Setting<Integer> maxSwitch = this.register(new Setting<Object>("Max Switch", Integer.valueOf(10), Integer.valueOf(0), Integer.valueOf(10), v -> this.pageSetting.getValue() == page.MAIN));
    public Setting<Boolean> switchmode = this.register(new Setting<Object>("KeyMode", Boolean.valueOf(false), v -> this.pageSetting.getValue() == page.MAIN));
    public Setting<Bind> SwitchBind = this.register(new Setting<Object>("SwitchKey", new Bind(-1), v -> this.switchmode.getValue() != false && this.pageSetting.getValue() == page.MAIN));
    public Setting<Float> switchHp = this.register(new Setting<Float>("SwitchHP", Float.valueOf(16.5f), Float.valueOf(0.1f), Float.valueOf(36.0f), v -> this.pageSetting.getValue() == page.MAIN));
    public Setting<Float> holeHP = this.register(new Setting<Float>("HoleHP", Float.valueOf(8.0f), Float.valueOf(0.1f), Float.valueOf(36.0f), v -> this.pageSetting.getValue() == page.MAIN));
    public Setting<Boolean> armorCheck = this.register(new Setting<Boolean>("ArmorCheck", Boolean.valueOf(false), v -> this.pageSetting.getValue() == page.MAIN));
    public Setting<Integer> actions = this.register(new Setting<Integer>("Packets", Integer.valueOf(4), Integer.valueOf(1), Integer.valueOf(4), v -> this.pageSetting.getValue() == page.MAIN));
    public Setting<Boolean> crystalCheck = this.register(new Setting<Boolean>("Crystal-Check", Boolean.valueOf(true), v -> this.pageSetting.getValue() == page.MAIN));
    public Setting<Boolean> totemElytra = this.register(new Setting<Object>("TotemElytra", Boolean.valueOf(false), v -> this.pageSetting.getValue() == page.MISC));
    public Setting<Boolean> notfromhotbar = this.register(new Setting<Object>("NoHotbar", Boolean.valueOf(false), v -> this.pageSetting.getValue() == page.MISC));
    public Setting<Boolean> fallcheck = this.register(new Setting<Object>("FallCheck", Boolean.valueOf(true), v -> this.pageSetting.getValue() == page.MISC));
    public Setting<Integer> falldistance = this.register(new Setting<Object>("FallDistance", Integer.valueOf(100), Integer.valueOf(1), Integer.valueOf(100), v -> this.fallcheck.getValue() != false && this.pageSetting.getValue() == page.MISC));
    public Setting<Boolean> antiPing = this.register(new Setting<Object>("Ping Predict", Boolean.valueOf(false), v -> this.pageSetting.getValue() == page.MISC));
    public Setting<Integer> pingvalue = this.register(new Setting<Object>("Ping Value", Integer.valueOf(200), Integer.valueOf(0), Integer.valueOf(1000), v -> this.antiPing.getValue() != false && this.pageSetting.getValue() == page.MISC));
    public Setting<Boolean> lagSwitch = this.register(new Setting<Object>("Anti Lag", Boolean.valueOf(false), v -> this.pageSetting.getValue() == page.MISC));
    public Setting<Boolean> debug = this.register(new Setting<Object>("Messages", Boolean.valueOf(false), v -> this.pageSetting.getValue() == page.MISC));
    String s = "OffhandRewrite";
    private boolean second;
    private boolean switchedForHealthReason;

    public OffhandRewrite() {
        super("OffhandRewrite", "Allows you to switch up your Offhand.", Module.Category.COMBAT, true, false, false);
        instance = this;
        this.taskList = new ConcurrentLinkedQueue<InventoryUtil.Task>();
        this.timer = new Timer();
        this.secondTimer = new Timer();
        this.thirdtimer = new Timer();
        this.currentMode = Mode2.TOTEMS;
        this.switchval = this.maxSwitch.getValue();
        this.totems = 0;
        this.crystals = 0;
        this.gapples = 0;
        this.lastTotemSlot = -1;
        this.lastGappleSlot = -1;
        this.lastCrystalSlot = -1;
        this.lastObbySlot = -1;
        this.lastWebSlot = -1;
        this.holdingCrystal = false;
        this.holdingTotem = false;
        this.holdingGapple = false;
        this.didSwitchThisTick = false;
        this.second = false;
        this.switchedForHealthReason = false;
    }

    public static OffhandRewrite getInstance() {
        if (instance == null) {
            instance = new OffhandRewrite();
        }
        return instance;
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(ProcessRightClickBlockEvent event) {
        if (event.hand == EnumHand.MAIN_HAND && event.stack.getItem() == Items.END_CRYSTAL && OffhandRewrite.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && OffhandRewrite.mc.objectMouseOver != null && event.pos == OffhandRewrite.mc.objectMouseOver.getBlockPos()) {
            event.setCanceled(true);
            OffhandRewrite.mc.player.setActiveHand(EnumHand.OFF_HAND);
            OffhandRewrite.mc.playerController.processRightClick((EntityPlayer)OffhandRewrite.mc.player, (World)OffhandRewrite.mc.world, EnumHand.OFF_HAND);
        }
    }

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKeyState() && this.switchmode.getValue().booleanValue() && this.SwitchBind.getValue().getKey() == Keyboard.getEventKey()) {
            if (this.switchval < this.maxSwitch.getValue()) {
                Mode2 newMode = (Mode2)EnumConverter.increaseEnum(this.currentMode);
                this.offhandmode.setValue(newMode);
                this.setMode(newMode);
                if (this.debug.getValue().booleanValue()) {
                    TextComponentString textComponentString = new TextComponentString(Experium.commandManager.getClientMessage() + " \u00a7r\u00a7aSwitched offhand to " + newMode.toString());
                    Notifications.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion((ITextComponent)textComponentString, this.s.length() * 10);
                }
                this.doSwitch();
            }
            if (this.debug.getValue().booleanValue()) {
                new TextComponentString(Experium.commandManager.getClientMessage() + " \u00a7r\u00a7aReached switch limit interval");
            }
        }
    }

    @Override
    public void onUpdate() {
        if (this.timer.passedMs(50L)) {
            if (OffhandRewrite.mc.player != null && OffhandRewrite.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && OffhandRewrite.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL && Mouse.isButtonDown((int)1)) {
                OffhandRewrite.mc.player.setActiveHand(EnumHand.OFF_HAND);
                OffhandRewrite.mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown((int)1);
            }
            this.switchval = 0;
        } else if (OffhandRewrite.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && OffhandRewrite.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
            OffhandRewrite.mc.gameSettings.keyBindUseItem.pressed = false;
        }
        if (OffhandRewrite.nullCheck()) {
            return;
        }
        this.doOffhand();
        if (this.secondTimer.passedMs(50L) && this.second) {
            this.second = false;
            this.timer.reset();
        }
        if (this.thirdtimer.passedDms(1000.0)) {
            this.switchval = 0;
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (!Feature.fullNullCheck() && OffhandRewrite.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && OffhandRewrite.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL && OffhandRewrite.mc.gameSettings.keyBindUseItem.isKeyDown()) {
            if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
                CPacketPlayerTryUseItemOnBlock packet2 = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
                if (packet2.getHand() == EnumHand.MAIN_HAND) {
                    if (this.timer.passedMs(50L)) {
                        OffhandRewrite.mc.player.setActiveHand(EnumHand.OFF_HAND);
                        OffhandRewrite.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(EnumHand.OFF_HAND));
                    }
                    event.setCanceled(true);
                }
            } else if (event.getPacket() instanceof CPacketPlayerTryUseItem && ((CPacketPlayerTryUseItem)event.getPacket()).getHand() == EnumHand.OFF_HAND && !this.timer.passedMs(50L)) {
                event.setCanceled(true);
            }
        }
    }

    @Override
    public String getDisplayInfo() {
        if (OffhandRewrite.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            return "Crystal";
        }
        if (OffhandRewrite.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            return "Totem";
        }
        if (OffhandRewrite.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE) {
            return "Gapple";
        }
        return null;
    }

    public void doOffhand() {
        this.didSwitchThisTick = false;
        this.holdingCrystal = OffhandRewrite.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
        this.holdingTotem = OffhandRewrite.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING;
        this.holdingGapple = OffhandRewrite.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE;
        this.totems = OffhandRewrite.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        if (this.holdingTotem) {
            this.totems += OffhandRewrite.mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        }
        this.crystals = OffhandRewrite.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.END_CRYSTAL).mapToInt(ItemStack::getCount).sum();
        if (this.holdingCrystal) {
            this.crystals += OffhandRewrite.mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.END_CRYSTAL).mapToInt(ItemStack::getCount).sum();
        }
        this.gapples = OffhandRewrite.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.GOLDEN_APPLE).mapToInt(ItemStack::getCount).sum();
        if (this.holdingGapple) {
            this.gapples += OffhandRewrite.mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.GOLDEN_APPLE).mapToInt(ItemStack::getCount).sum();
        }
        ++this.switchval;
        if (this.switchval < 6) {
            this.doSwitch();
        }
    }

    public void doSwitch() {
        this.currentMode = Mode2.TOTEMS;
        if (this.swordgap.getValue().booleanValue() && OffhandRewrite.mc.player.getHealth() > this.switchHp.getValue().floatValue() && OffhandRewrite.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword) {
            this.currentMode = Mode2.GAPPLES;
        } else if (this.rightGap.getValue().booleanValue() && OffhandRewrite.mc.player.getHealth() > this.switchHp.getValue().floatValue() && OffhandRewrite.mc.gameSettings.keyBindUseItem.isKeyDown() && OffhandRewrite.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && !(OffhandRewrite.mc.currentScreen instanceof GuiContainer) && !(OffhandRewrite.mc.currentScreen instanceof GuiChat) && !(OffhandRewrite.mc.currentScreen instanceof ExperiumGui)) {
            this.currentMode = Mode2.GAPPLES;
        } else if (this.currentMode != Mode2.CRYSTALS && this.offhandmode.getValue() == Mode2.CRYSTALS && (EntityUtil.isSafe((Entity)OffhandRewrite.mc.player) && EntityUtil.getHealth((Entity)OffhandRewrite.mc.player, true) > this.holeHP.getValue().floatValue() || EntityUtil.getHealth((Entity)OffhandRewrite.mc.player, true) > this.switchHp.getValue().floatValue())) {
            this.currentMode = Mode2.CRYSTALS;
        } else if (this.currentMode != Mode2.GAPPLES && this.offhandmode.getValue() == Mode2.GAPPLES && (EntityUtil.isSafe((Entity)OffhandRewrite.mc.player) && EntityUtil.getHealth((Entity)OffhandRewrite.mc.player, true) > this.holeHP.getValue().floatValue() || EntityUtil.getHealth((Entity)OffhandRewrite.mc.player, true) > this.switchHp.getValue().floatValue())) {
            this.currentMode = Mode2.GAPPLES;
        }
        if (this.currentMode == Mode2.CRYSTALS && this.crystals == 0) {
            if (this.gapples != 0) {
                this.setMode(Mode2.GAPPLES);
            }
            this.setMode(Mode2.TOTEMS);
        }
        if (this.currentMode == Mode2.CRYSTALS && (!EntityUtil.isSafe((Entity)OffhandRewrite.mc.player) && EntityUtil.getHealth((Entity)OffhandRewrite.mc.player, true) <= this.switchHp.getValue().floatValue() || EntityUtil.getHealth((Entity)OffhandRewrite.mc.player, true) <= this.holeHP.getValue().floatValue())) {
            if (this.currentMode == Mode2.CRYSTALS) {
                this.switchedForHealthReason = true;
            }
            this.setMode(Mode2.TOTEMS);
        }
        if (this.currentMode == Mode2.CRYSTALS && (!EntityUtil.isSafe((Entity)OffhandRewrite.mc.player) && EntityUtil.getHealth((Entity)OffhandRewrite.mc.player, true) <= this.switchHp.getValue().floatValue() || EntityUtil.getHealth((Entity)OffhandRewrite.mc.player, true) <= this.holeHP.getValue().floatValue())) {
            if (this.currentMode == Mode2.CRYSTALS) {
                this.switchedForHealthReason = true;
            }
            this.setMode(Mode2.TOTEMS);
        }
        if (OffhandRewrite.mc.player.isAirBorne && this.totemElytra.getValue().booleanValue() && OffhandRewrite.mc.player.isElytraFlying() && OffhandRewrite.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
            this.setMode(Mode2.TOTEMS);
        }
        if (OffhandRewrite.mc.player.fallDistance > (float)this.falldistance.getValue().intValue() && this.fallcheck.getValue().booleanValue()) {
            this.setMode(Mode2.TOTEMS);
        }
        if (this.switchedForHealthReason && (EntityUtil.isSafe((Entity)OffhandRewrite.mc.player) && EntityUtil.getHealth((Entity)OffhandRewrite.mc.player, true) > this.holeHP.getValue().floatValue() || EntityUtil.getHealth((Entity)OffhandRewrite.mc.player, true) > this.switchHp.getValue().floatValue())) {
            this.setMode(this.currentMode);
            this.switchedForHealthReason = false;
        }
        if (this.currentMode == Mode2.CRYSTALS && this.armorCheck.getValue().booleanValue() && (OffhandRewrite.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.AIR || OffhandRewrite.mc.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == Items.AIR || OffhandRewrite.mc.player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() == Items.AIR || OffhandRewrite.mc.player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() == Items.AIR)) {
            this.setMode(this.currentMode);
        }
        if (this.crystalCheck.getValue().booleanValue() && this.calcCrystal()) {
            if (this.debug.getValue().booleanValue()) {
                TextComponentString textComponentString = new TextComponentString(Experium.commandManager.getClientMessage() + " \u00a7r\u00a7aSwitched to totem because lethal crystal");
                Notifications.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion((ITextComponent)textComponentString, this.s.length() * 10);
            }
            this.switchedForHealthReason = true;
            this.currentMode = Mode2.TOTEMS;
        }
        if (OffhandRewrite.mc.currentScreen instanceof GuiContainer && !(OffhandRewrite.mc.currentScreen instanceof GuiInventory)) {
            return;
        }
        if (this.antiPing.getValue().booleanValue() && (PingBypass.getInstance().isConnected() ? PingBypass.getInstance().getServerPing() : (long)Experium.serverManager.getPing()) > (long)this.pingvalue.getValue().intValue()) {
            this.setMode(Mode2.TOTEMS);
        }
        if (this.lagSwitch.getValue().booleanValue() && Experium.serverManager.isServerNotResponding()) {
            this.setMode(Mode2.TOTEMS);
        }
        Item currentOffhandItem = OffhandRewrite.mc.player.getHeldItemOffhand().getItem();
        switch (this.currentMode) {
            case TOTEMS: {
                if (this.totems <= 0 || this.holdingTotem) break;
                this.lastTotemSlot = InventoryUtil.findItemInventorySlot(Items.TOTEM_OF_UNDYING, false);
                int lastSlot = this.getLastSlot(currentOffhandItem, this.lastTotemSlot);
                this.putItemInOffhand(this.lastTotemSlot, lastSlot);
                break;
            }
            case GAPPLES: {
                if (this.gapples <= 0 || this.holdingGapple) break;
                this.lastGappleSlot = InventoryUtil.findItemInventorySlot(Items.GOLDEN_APPLE, false);
                int lastSlot = this.getLastSlot(currentOffhandItem, this.lastGappleSlot);
                this.putItemInOffhand(this.lastGappleSlot, lastSlot);
                break;
            }
            default: {
                if (this.crystals <= 0 || this.holdingCrystal) break;
                this.lastCrystalSlot = InventoryUtil.findItemInventorySlot(Items.END_CRYSTAL, false);
                int lastSlot = this.getLastSlot(currentOffhandItem, this.lastCrystalSlot);
                this.putItemInOffhand(this.lastCrystalSlot, lastSlot);
                break;
            }
        }
        for (int i = 0; i < this.actions.getValue(); ++i) {
            InventoryUtil.Task task = this.taskList.poll();
            if (task == null) continue;
            task.run();
            if (!task.isSwitching()) continue;
            this.didSwitchThisTick = true;
        }
    }

    private int getLastSlot(Item item, int slotIn) {
        if (item == Items.END_CRYSTAL) {
            return this.lastCrystalSlot;
        }
        if (item == Items.GOLDEN_APPLE) {
            return this.lastGappleSlot;
        }
        if (item == Items.TOTEM_OF_UNDYING) {
            return this.lastTotemSlot;
        }
        if (InventoryUtil.isBlock(item, BlockObsidian.class)) {
            return this.lastObbySlot;
        }
        if (InventoryUtil.isBlock(item, BlockWeb.class)) {
            return this.lastWebSlot;
        }
        if (item == Items.AIR) {
            return -1;
        }
        return slotIn;
    }

    private void putItemInOffhand(int slotIn, int slotOut) {
        if (slotIn != -1 && this.taskList.isEmpty()) {
            this.taskList.add(new InventoryUtil.Task(slotIn));
            this.taskList.add(new InventoryUtil.Task(45));
            this.taskList.add(new InventoryUtil.Task(slotOut));
            this.taskList.add(new InventoryUtil.Task());
        }
    }

    public void setMode(Mode2 mode) {
        this.currentMode = this.currentMode == mode ? Mode2.TOTEMS : mode;
    }

    public boolean calcCrystal() {
        for (Entity t : OffhandRewrite.mc.world.loadedEntityList) {
            if (!(t instanceof EntityEnderCrystal) || !(OffhandRewrite.mc.player.getDistanceSq(t.getPosition()) <= 36.0) || !(DamageUtil.calculateDamage(t, (Entity)OffhandRewrite.mc.player) >= OffhandRewrite.mc.player.getHealth())) continue;
            return true;
        }
        return false;
    }

    public static enum page {
        MAIN,
        MISC;

    }

    public static enum Mode2 {
        TOTEMS,
        GAPPLES,
        CRYSTALS;

    }
}

