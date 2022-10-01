package dev._3000IQPlay.experium.features.modules.combat;

import com.mojang.authlib.GameProfile;
import io.netty.util.internal.ConcurrentSet;
import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.event.events.ClientEvent;
import dev._3000IQPlay.experium.event.events.PacketEvent;
import dev._3000IQPlay.experium.event.events.Render3DEvent;
import dev._3000IQPlay.experium.event.events.UpdateWalkingPlayerEvent;
import dev._3000IQPlay.experium.features.command.Command;
import dev._3000IQPlay.experium.features.gui.ExperiumGui;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.modules.combat.AutoTrap;
import dev._3000IQPlay.experium.features.modules.combat.Offhand;
import dev._3000IQPlay.experium.features.modules.combat.Surround;
import dev._3000IQPlay.experium.features.modules.misc.NoSoundLag;
import dev._3000IQPlay.experium.features.setting.Bind;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.util.BlockUtil;
import dev._3000IQPlay.experium.util.ColorUtil;
import dev._3000IQPlay.experium.util.DamageUtil;
import dev._3000IQPlay.experium.util.EntityUtil;
import dev._3000IQPlay.experium.util.InventoryUtil;
import dev._3000IQPlay.experium.util.MathUtil;
import dev._3000IQPlay.experium.util.RenderUtil;
import dev._3000IQPlay.experium.util.Timer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class AutoCrystal
        extends Module {
    public static EntityPlayer target;
    public static Set<BlockPos> lowDmgPos;
    public static Set<BlockPos> placedPos;
    public static Set<BlockPos> brokenPos;
    private static AutoCrystal instance;
    public final Timer yawStepTimer = new Timer();
    private final Timer switchTimer = new Timer();
    private final Timer manualTimer = new Timer();
    private final Timer breakTimer = new Timer();
    private final Timer placeTimer = new Timer();
    private final Timer syncTimer = new Timer();
    private final Timer predictTimer = new Timer();
    private final Timer renderTimer = new Timer();
    private final AtomicBoolean shouldInterrupt = new AtomicBoolean(false);
    private final Timer syncroTimer = new Timer();
    private final Map<EntityPlayer, Timer> totemPops = new ConcurrentHashMap<EntityPlayer, Timer>();
    private final Queue<CPacketUseEntity> packetUseEntities = new LinkedList<CPacketUseEntity>();
    private final AtomicBoolean threadOngoing = new AtomicBoolean(false);
    private final List<RenderPos> positions = new ArrayList<RenderPos>();
    private final Setting<Settings> setting = this.register(new Setting<Settings>("Settings", Settings.PLACE));
    public final Setting<Boolean> attackOppositeHand = this.register(new Setting<Object>("OppositeHand", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV));
    public final Setting<Boolean> removeAfterAttack = this.register(new Setting<Object>("AttackRemove", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV));
    public final Setting<Boolean> antiBlock = this.register(new Setting<Object>("AntiFeetPlace", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV));
    private final Setting<Integer> eventMode = this.register(new Setting<Object>("Updates", Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(3), v -> this.setting.getValue() == Settings.DEV));
    private final Setting<Integer> switchCooldown = this.register(new Setting<Object>("Cooldown", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(1000), v -> this.setting.getValue() == Settings.MISC));
    public Setting<Boolean> place = this.register(new Setting<Object>("Place", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.PLACE));
    public Setting<Integer> placeDelay = this.register(new Setting<Object>("PlaceDelay", Integer.valueOf(25), Integer.valueOf(0), Integer.valueOf(500), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false));
    public Setting<Float> placeRange = this.register(new Setting<Object>("PlaceRange", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(10.0f), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false));
    public Setting<Float> minDamage = this.register(new Setting<Object>("MinDamage", Float.valueOf(7.0f), Float.valueOf(0.1f), Float.valueOf(20.0f), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false));
    public Setting<Float> maxSelfPlace = this.register(new Setting<Object>("MaxSelfPlace", Float.valueOf(5.0f), Float.valueOf(0.1f), Float.valueOf(36.0f), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false));
    public Setting<Integer> wasteAmount = this.register(new Setting<Object>("WasteAmount", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(5), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false));
    public Setting<Boolean> wasteMinDmgCount = this.register(new Setting<Object>("CountMinDmg", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false));
    public Setting<Float> facePlace = this.register(new Setting<Object>("FacePlace", Float.valueOf(10.0f), Float.valueOf(0.1f), Float.valueOf(36.0f), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false));
    public Setting<Boolean> antiSurround = this.register(new Setting<Object>("AntiSurround", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false));
    public Setting<Boolean> limitFacePlace = this.register(new Setting<Object>("LimitFacePlace", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false));
    public Setting<Boolean> oneDot15 = this.register(new Setting<Object>("1.15", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false));
    public Setting<Boolean> doublePop = this.register(new Setting<Object>("AntiTotem", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false));
    public Setting<Double> popHealth = this.register(new Setting<Object>("PopHealth", Double.valueOf(1.0), Double.valueOf(0.0), Double.valueOf(3.0), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false && this.doublePop.getValue() != false));
    public Setting<Float> popDamage = this.register(new Setting<Object>("PopDamage", Float.valueOf(4.0f), Float.valueOf(0.0f), Float.valueOf(6.0f), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false && this.doublePop.getValue() != false));
    public Setting<Integer> popTime = this.register(new Setting<Object>("PopTime", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(1000), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false && this.doublePop.getValue() != false));
    public Setting<Float> minMinDmg = this.register(new Setting<Object>("MinMinDmg", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(3.0f), v -> this.setting.getValue() == Settings.DEV && this.place.getValue() != false));
    public Setting<Boolean> explode = this.register(new Setting<Object>("Break", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.BREAK));
    public Setting<Switch> switchMode = this.register(new Setting<Object>("Attack", (Object)Switch.BREAKSLOT, v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() != false));
    public Setting<Integer> breakDelay = this.register(new Setting<Object>("BreakDelay", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(500), v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() != false));
	public Setting<AntiWeaknessMode> antiWeakness = this.register(new Setting<>("Anti Weakness", AntiWeaknessMode.Silent, v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue()));
    public Setting<Float> breakRange = this.register(new Setting<Object>("BreakRange", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(10.0f), v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() != false));
    public Setting<Integer> packets = this.register(new Setting<Object>("Packets", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(6), v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() != false));
    public Setting<Float> maxSelfBreak = this.register(new Setting<Object>("MaxSelfBreak", Float.valueOf(10.0f), Float.valueOf(0.1f), Float.valueOf(36.0f), v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() != false));
    public Setting<Boolean> instant = this.register(new Setting<Object>("Predict", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() != false && this.place.getValue() != false));
    public Setting<PredictTimer> instantTimer = this.register(new Setting<Object>("PredictTimer", (Object)PredictTimer.NONE, v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() != false && this.place.getValue() != false && this.instant.getValue() != false));
    public Setting<Integer> predictDelay = this.register(new Setting<Object>("PredictDelay", Integer.valueOf(12), Integer.valueOf(0), Integer.valueOf(500), v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() != false && this.place.getValue() != false && this.instant.getValue() != false && this.instantTimer.getValue() == PredictTimer.PREDICT));
    public Setting<Boolean> resetBreakTimer = this.register(new Setting<Object>("ResetBreakTimer", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() != false && this.place.getValue() != false && this.instant.getValue() != false));
    public Setting<Boolean> predictCalc = this.register(new Setting<Object>("PredictCalc", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() != false && this.place.getValue() != false && this.instant.getValue() != false));
    public Setting<Boolean> superSafe = this.register(new Setting<Object>("SuperSafe", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() != false && this.place.getValue() != false && this.instant.getValue() != false));
    public Setting<Boolean> antiCommit = this.register(new Setting<Object>("AntiOverCommit", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() != false && this.place.getValue() != false && this.instant.getValue() != false));
    public Setting<Boolean> manual = this.register(new Setting<Object>("Manual", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.BREAK));
    public Setting<Boolean> manualMinDmg = this.register(new Setting<Object>("ManMinDmg", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.BREAK && this.manual.getValue() != false));
    public Setting<Integer> manualBreak = this.register(new Setting<Object>("ManualDelay", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(500), v -> this.setting.getValue() == Settings.BREAK && this.manual.getValue() != false));
    public Setting<Boolean> sync = this.register(new Setting<Object>("Sync", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.BREAK && (this.explode.getValue() != false || this.manual.getValue() != false)));
    public Setting<Boolean> render = this.register(new Setting<Object>("Render", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.RENDER));
    public Setting<Boolean> justRender = this.register(new Setting<Object>("JustRender", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false));
    public Setting<Boolean> fakeSwing = this.register(new Setting<Object>("FakeSwing", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV && this.justRender.getValue() != false));
    public Setting<RenderMode> renderMode = this.register(new Setting<RenderMode>("Mode", RenderMode.STATIC, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false));
	public final Setting<Float> rainbowBrightness = this.register(new Setting<Object>("Brightness", Float.valueOf(150.0f), Float.valueOf(1.0f), Float.valueOf(255.0f), v -> this.setting.getValue() == Settings.RENDER && this.renderMode.getValue() == RenderMode.GRADIENTRAINBOW));
    public final Setting<Float> rainbowSaturation = this.register(new Setting<Object>("Saturation", Float.valueOf(150.0f), Float.valueOf(1.0f), Float.valueOf(255.0f), v -> this.setting.getValue() == Settings.RENDER && this.renderMode.getValue() == RenderMode.GRADIENTRAINBOW));
    private final Setting<Boolean> fadeFactor = this.register(new Setting<Boolean>("Fade", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.RENDER && this.renderMode.getValue() == RenderMode.FADE && this.render.getValue() != false));
    private final Setting<Boolean> scaleFactor = this.register(new Setting<Boolean>("Shrink", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.RENDER && this.renderMode.getValue() == RenderMode.FADE && this.render.getValue() != false));
    private final Setting<Boolean> slabFactor = this.register(new Setting<Boolean>("Slab", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.RENDER && this.renderMode.getValue() == RenderMode.FADE && this.render.getValue() != false));
    private final Setting<Boolean> onlyplaced = this.register(new Setting<Boolean>("OnlyPlaced", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.RENDER && this.renderMode.getValue() == RenderMode.FADE && this.render.getValue() != false));
    private final Setting<Float> duration = this.register(new Setting<Float>("Duration", Float.valueOf(1500.0f), Float.valueOf(0.0f), Float.valueOf(5000.0f), v -> this.setting.getValue() == Settings.RENDER && this.renderMode.getValue() == RenderMode.FADE && this.render.getValue() != false));
    private final Setting<Integer> max = this.register(new Setting<Integer>("MaxPositions", Integer.valueOf(15), Integer.valueOf(1), Integer.valueOf(30), v -> this.setting.getValue() == Settings.RENDER && this.renderMode.getValue() == RenderMode.FADE && this.render.getValue() != false));
    private final Setting<Float> slabHeight = this.register(new Setting<Float>("SlabDepth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(1.0f), v -> this.setting.getValue() == Settings.RENDER && (this.renderMode.getValue() == RenderMode.STATIC || this.renderMode.getValue() == RenderMode.GLIDE) && this.render.getValue() != false));
    private final Setting<Float> moveSpeed = this.register(new Setting<Float>("Speed", Float.valueOf(900.0f), Float.valueOf(0.0f), Float.valueOf(1500.0f), v -> this.setting.getValue() == Settings.RENDER && this.renderMode.getValue() == RenderMode.GLIDE && this.render.getValue() != false));
    private final Setting<Float> accel = this.register(new Setting<Float>("Deceleration", Float.valueOf(0.8f), Float.valueOf(0.0f), Float.valueOf(1.0f), v -> this.setting.getValue() == Settings.RENDER && this.renderMode.getValue() == RenderMode.GLIDE && this.render.getValue() != false));
    public Setting<Boolean> colorSync = this.register(new Setting<Object>("CSync", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false));
    public Setting<Boolean> box = this.register(new Setting<Object>("Box", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false));
    private final Setting<Integer> bRed = this.register(new Setting<Object>("BoxRed", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false && this.box.getValue() != false));
    private final Setting<Integer> bGreen = this.register(new Setting<Object>("BoxGreen", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false && this.box.getValue() != false));
    private final Setting<Integer> bBlue = this.register(new Setting<Object>("BoxBlue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false && this.box.getValue() != false));
    private final Setting<Integer> bAlpha = this.register(new Setting<Object>("BoxAlpha", Integer.valueOf(40), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false && this.box.getValue() != false));
    public Setting<Boolean> outline = this.register(new Setting<Object>("Outline", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false));
    private final Setting<Integer> oRed = this.register(new Setting<Object>("OutlineRed", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false && this.outline.getValue() != false));
    private final Setting<Integer> oGreen = this.register(new Setting<Object>("OutlineGreen", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false && this.outline.getValue() != false));
    private final Setting<Integer> oBlue = this.register(new Setting<Object>("OutlineBlue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false && this.outline.getValue() != false));
    private final Setting<Integer> oAlpha = this.register(new Setting<Object>("OutlineAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false && this.outline.getValue() != false));
    private final Setting<Float> lineWidth = this.register(new Setting<Object>("LineWidth", Float.valueOf(1.5f), Float.valueOf(0.1f), Float.valueOf(5.0f), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false && this.outline.getValue() != false));
    public Setting<Boolean> text = this.register(new Setting<Object>("Text", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false));
    public Setting<Boolean> holdFacePlace = this.register(new Setting<Object>("HoldFacePlace", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.MISC));
    public Setting<Boolean> holdFaceBreak = this.register(new Setting<Object>("HoldSlowBreak", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.MISC && this.holdFacePlace.getValue() != false));
    public Setting<Boolean> slowFaceBreak = this.register(new Setting<Object>("SlowFaceBreak", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.MISC));
    public Setting<Boolean> actualSlowBreak = this.register(new Setting<Object>("ActuallySlow", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.MISC));
    public Setting<Integer> facePlaceSpeed = this.register(new Setting<Object>("FaceSpeed", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(500), v -> this.setting.getValue() == Settings.MISC));
    public Setting<Boolean> antiNaked = this.register(new Setting<Object>("AntiNaked", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.MISC));
	public Setting<Timing> timing = this.register(new Setting<>("Timing", Timing.Sequential, v -> this.setting.getValue() == Settings.MISC));
    public Setting<Float> range = this.register(new Setting<Object>("Range", Float.valueOf(12.0f), Float.valueOf(0.1f), Float.valueOf(20.0f), v -> this.setting.getValue() == Settings.MISC));
    public Setting<Target> targetMode = this.register(new Setting<Object>("Target", (Object)Target.CLOSEST, v -> this.setting.getValue() == Settings.MISC));
    public Setting<Boolean> doublePopOnDamage = this.register(new Setting<Object>("DamagePop", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false && this.doublePop.getValue() != false && this.targetMode.getValue() == Target.DAMAGE));
    public Setting<Boolean> webAttack = this.register(new Setting<Object>("WebAttack", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.MISC && this.targetMode.getValue() != Target.DAMAGE));
    public Setting<Integer> minArmor = this.register(new Setting<Object>("MinArmor", Integer.valueOf(5), Integer.valueOf(0), Integer.valueOf(125), v -> this.setting.getValue() == Settings.MISC));
    public Setting<AutoSwitch> autoSwitch = this.register(new Setting<Object>("Switch", (Object)AutoSwitch.TOGGLE, v -> this.setting.getValue() == Settings.MISC));
    public Setting<Bind> switchBind = this.register(new Setting<Object>("SwitchBind", new Bind(-1), v -> this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() == AutoSwitch.TOGGLE));
    public Setting<Boolean> offhandSwitch = this.register(new Setting<Object>("Offhand", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE && this.autoSwitch.getValue() != AutoSwitch.SILENT));
    public Setting<Boolean> switchBack = this.register(new Setting<Object>("Switchback", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE && this.offhandSwitch.getValue() != false && this.autoSwitch.getValue() != AutoSwitch.SILENT));
    public Setting<Boolean> lethalSwitch = this.register(new Setting<Object>("LethalSwitch", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE && this.autoSwitch.getValue() != AutoSwitch.SILENT));
    public Setting<Boolean> mineSwitch = this.register(new Setting<Object>("MineSwitch", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE && this.autoSwitch.getValue() != AutoSwitch.SILENT));
    public Setting<Rotate> rotate = this.register(new Setting<Object>("Rotate", (Object)Rotate.OFF, v -> this.setting.getValue() == Settings.MISC));
    public Setting<Boolean> YawStep = this.register(new Setting<Boolean>("YawStep", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.MISC && this.rotate.getValue() != Rotate.OFF));
    public Setting<Integer> YawStepVal = this.register(new Setting<Object>("YawStepValue", Integer.valueOf(45), Integer.valueOf(0), Integer.valueOf(180), v -> this.setting.getValue() == Settings.MISC && this.rotate.getValue() != Rotate.OFF && this.YawStep.getValue() != false));
    public Setting<Integer> YawStepTicks = this.register(new Setting<Integer>("YawStepTicks", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(20), v -> this.setting.getValue() == Settings.MISC && this.rotate.getValue() != Rotate.OFF && this.YawStep.getValue() != false));
    public Setting<Boolean> YawStepDebugMessages = this.register(new Setting<Boolean>("YawStep Debug", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.MISC && this.rotate.getValue() != Rotate.OFF && this.YawStep.getValue() != false));
    public Setting<Boolean> rotateFirst = this.register(new Setting<Object>("FirstRotation", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV && this.rotate.getValue() != Rotate.OFF && this.eventMode.getValue() == 2));
    public Setting<Boolean> suicide = this.register(new Setting<Object>("Suicide", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.MISC));
    public Setting<Boolean> fullCalc = this.register(new Setting<Object>("ExtraCalc", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.MISC));
    public Setting<Boolean> sound = this.register(new Setting<Object>("Sound", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.MISC));
    public Setting<Float> soundRange = this.register(new Setting<Object>("SoundRange", Float.valueOf(12.0f), Float.valueOf(0.0f), Float.valueOf(12.0f), v -> this.setting.getValue() == Settings.MISC));
    public Setting<Float> soundPlayer = this.register(new Setting<Object>("SoundPlayer", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(12.0f), v -> this.setting.getValue() == Settings.MISC));
    public Setting<Boolean> soundConfirm = this.register(new Setting<Object>("SoundConfirm", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.MISC));
    public Setting<Boolean> extraSelfCalc = this.register(new Setting<Object>("MinSelfDmg", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.MISC));
    public Setting<AntiFriendPop> antiFriendPop = this.register(new Setting<Object>("AntiFriendPop", (Object)AntiFriendPop.NONE, v -> this.setting.getValue() == Settings.MISC));
    public Setting<Boolean> noCount = this.register(new Setting<Object>("AntiCount", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.MISC && (this.antiFriendPop.getValue() == AntiFriendPop.ALL || this.antiFriendPop.getValue() == AntiFriendPop.BREAK)));
    public Setting<Boolean> calcEvenIfNoDamage = this.register(new Setting<Object>("BigFriendCalc", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.MISC && (this.antiFriendPop.getValue() == AntiFriendPop.ALL || this.antiFriendPop.getValue() == AntiFriendPop.BREAK) && this.targetMode.getValue() != Target.DAMAGE));
    public Setting<Boolean> predictFriendDmg = this.register(new Setting<Object>("PredictFriend", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.MISC && (this.antiFriendPop.getValue() == AntiFriendPop.ALL || this.antiFriendPop.getValue() == AntiFriendPop.BREAK) && this.instant.getValue() != false));
    public Setting<Raytrace> raytrace = this.register(new Setting<Object>("Raytrace", (Object)Raytrace.NONE, v -> this.setting.getValue() == Settings.MISC));
    public Setting<Float> placetrace = this.register(new Setting<Object>("Placetrace", Float.valueOf(4.5f), Float.valueOf(0.0f), Float.valueOf(10.0f), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false && this.raytrace.getValue() != Raytrace.NONE && this.raytrace.getValue() != Raytrace.BREAK));
    public Setting<Float> breaktrace = this.register(new Setting<Object>("Breaktrace", Float.valueOf(4.5f), Float.valueOf(0.0f), Float.valueOf(10.0f), v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() != false && this.raytrace.getValue() != Raytrace.NONE && this.raytrace.getValue() != Raytrace.PLACE));
    public Setting<Boolean> breakSwing = this.register(new Setting<Object>("BreakSwing", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.DEV));
    public Setting<Boolean> placeSwing = this.register(new Setting<Object>("PlaceSwing", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV));
    public Setting<Boolean> exactHand = this.register(new Setting<Object>("ExactHand", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV && this.placeSwing.getValue() != false));
    public Setting<Logic> logic = this.register(new Setting<Object>("Logic", (Object)Logic.BREAKPLACE, v -> this.setting.getValue() == Settings.DEV));
    public Setting<DamageSync> damageSync = this.register(new Setting<Object>("DamageSync", (Object)DamageSync.NONE, v -> this.setting.getValue() == Settings.DEV));
    public Setting<Integer> damageSyncTime = this.register(new Setting<Object>("SyncDelay", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(500), v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE));
    public Setting<Float> dropOff = this.register(new Setting<Object>("DropOff", Float.valueOf(5.0f), Float.valueOf(0.0f), Float.valueOf(10.0f), v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() == DamageSync.BREAK));
    public Setting<Integer> confirm = this.register(new Setting<Object>("Confirm", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(1000), v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE));
    public Setting<Boolean> syncedFeetPlace = this.register(new Setting<Object>("FeetSync", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE));
    public Setting<Boolean> fullSync = this.register(new Setting<Object>("FullSync", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && this.syncedFeetPlace.getValue() != false));
    public Setting<Boolean> syncCount = this.register(new Setting<Object>("SyncCount", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && this.syncedFeetPlace.getValue() != false));
    public Setting<Boolean> hyperSync = this.register(new Setting<Object>("HyperSync", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && this.syncedFeetPlace.getValue() != false));
    public Setting<Boolean> gigaSync = this.register(new Setting<Object>("GigaSync", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && this.syncedFeetPlace.getValue() != false));
    public Setting<Boolean> syncySync = this.register(new Setting<Object>("SyncySync", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && this.syncedFeetPlace.getValue() != false));
    public Setting<Boolean> enormousSync = this.register(new Setting<Object>("EnormousSync", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && this.syncedFeetPlace.getValue() != false));
    public Setting<Boolean> holySync = this.register(new Setting<Object>("UnbelievableSync", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && this.syncedFeetPlace.getValue() != false));
    public Setting<ThreadMode> threadMode = this.register(new Setting<Object>("Thread", (Object)ThreadMode.NONE, v -> this.setting.getValue() == Settings.DEV));
    public Setting<Integer> threadDelay = this.register(new Setting<Object>("ThreadDelay", Integer.valueOf(50), Integer.valueOf(1), Integer.valueOf(1000), v -> this.setting.getValue() == Settings.DEV && this.threadMode.getValue() != ThreadMode.NONE));
    public Setting<Boolean> syncThreadBool = this.register(new Setting<Object>("ThreadSync", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.DEV && this.threadMode.getValue() != ThreadMode.NONE));
    public Setting<Integer> syncThreads = this.register(new Setting<Object>("SyncThreads", Integer.valueOf(1000), Integer.valueOf(1), Integer.valueOf(10000), v -> this.setting.getValue() == Settings.DEV && this.threadMode.getValue() != ThreadMode.NONE && this.syncThreadBool.getValue() != false));
    public Setting<Boolean> predictPos = this.register(new Setting<Object>("PredictPos", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV));
    public Setting<Integer> predictTicks = this.register(new Setting<Object>("ExtrapolationTicks", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(20), v -> this.setting.getValue() == Settings.DEV && this.predictPos.getValue() != false));
    public Setting<Integer> rotations = this.register(new Setting<Object>("Spoofs", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(20), v -> this.setting.getValue() == Settings.DEV));
    public Setting<Boolean> predictRotate = this.register(new Setting<Object>("PredictRotate", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV));
    public Setting<Float> predictOffset = this.register(new Setting<Object>("PredictOffset", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(4.0f), v -> this.setting.getValue() == Settings.DEV));
    public boolean rotating;
    private Queue<Entity> attackList = new ConcurrentLinkedQueue<Entity>();
    private Map<Entity, Float> crystalMap = new HashMap<Entity, Float>();
    private Entity efficientTarget;
    private double currentDamage;
    private double renderDamage;
    private double lastDamage;
    private boolean didRotation;
    private boolean switching;
    private BlockPos placePos;
    private BlockPos renderPos;
    private boolean mainHand;
    private boolean offHand;
    private int crystalCount;
    private int minDmgCount;
    private int lastSlot = -1;
    private float yaw;
    private float pitch;
    private BlockPos webPos;
    private BlockPos lastPos;
    private boolean posConfirmed;
    private boolean foundDoublePop;
    private int rotationPacketsSpoofed;
    private ScheduledExecutorService executor;
    private Thread thread;
    private EntityPlayer currentSyncTarget;
    private BlockPos syncedPlayerPos;
    private BlockPos syncedCrystalPos;
    private PlaceInfo placeInfo;
    private boolean addTolowDmg;
    private boolean shouldSilent;
    private BlockPos lastRenderPos;
    private AxisAlignedBB renderBB;
    private float timePassed;

    public AutoCrystal() {
        super("AutoCrystal", "Does this need a explanation?", Module.Category.COMBAT, true, false, false);
        instance = this;
    }

    public static AutoCrystal getInstance() {
        if (instance == null) {
            instance = new AutoCrystal();
        }
        return instance;
    }

    @Override
    public void onTick() {
        if (this.threadMode.getValue() == ThreadMode.NONE && this.eventMode.getValue() == 3) {
            this.doAutoCrystal();
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 1) {
            this.postProcessing();
        }
        if (event.getStage() != 0) {
            return;
        }
        if (this.eventMode.getValue() == 2) {
            this.doAutoCrystal();
        }
    }

    public void postTick() {
        if (this.threadMode.getValue() != ThreadMode.NONE) {
            this.processMultiThreading();
        }
    }

    @Override
    public void onUpdate() {
        if (this.threadMode.getValue() == ThreadMode.NONE && this.eventMode.getValue() == 1) {
            this.doAutoCrystal();
        }
    }

    @Override
    public void onToggle() {
        brokenPos.clear();
        placedPos.clear();
        this.totemPops.clear();
        this.rotating = false;
    }

    @Override
    public void onDisable() {
        this.positions.clear();
        this.lastRenderPos = null;
        if (this.thread != null) {
            this.shouldInterrupt.set(true);
        }
        if (this.executor != null) {
            this.executor.shutdown();
        }
    }

    @Override
    public void onEnable() {
        if (this.threadMode.getValue() != ThreadMode.NONE) {
            this.processMultiThreading();
        }
    }

    @Override
    public String getDisplayInfo() {
        if (this.switching) {
            return "\u00a7aSwitch";
        }
        if (target != null) {
            return target.getName();
        }
        return null;
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        CPacketUseEntity packet;
        if (event.getStage() == 0 && this.rotate.getValue() != Rotate.OFF && this.rotating && this.eventMode.getValue() != 2 && event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packet2 = (CPacketPlayer)event.getPacket();
            packet2.yaw = this.yaw;
            packet2.pitch = this.pitch;
            ++this.rotationPacketsSpoofed;
            if (this.rotationPacketsSpoofed >= this.rotations.getValue()) {
                this.rotating = false;
                this.rotationPacketsSpoofed = 0;
            }
        }
        BlockPos pos = null;
        if (event.getStage() == 0 && event.getPacket() instanceof CPacketUseEntity && (packet = (CPacketUseEntity)event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK && packet.getEntityFromWorld((World)AutoCrystal.mc.world) instanceof EntityEnderCrystal) {
            pos = Objects.requireNonNull(packet.getEntityFromWorld((World)AutoCrystal.mc.world)).getPosition();
            if (this.removeAfterAttack.getValue().booleanValue()) {
                Objects.requireNonNull(packet.getEntityFromWorld((World)AutoCrystal.mc.world)).setDead();
                AutoCrystal.mc.world.removeEntityFromWorld(packet.entityId);
            }
        }
        if (event.getStage() == 0 && event.getPacket() instanceof CPacketUseEntity && (packet = (CPacketUseEntity)event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK && packet.getEntityFromWorld((World)AutoCrystal.mc.world) instanceof EntityEnderCrystal) {
            EntityEnderCrystal crystal = (EntityEnderCrystal)packet.getEntityFromWorld((World)AutoCrystal.mc.world);
            if (this.antiBlock.getValue().booleanValue() && EntityUtil.isCrystalAtFeet(crystal, this.range.getValue().floatValue()) && pos != null) {
                this.rotateToPos(pos);
                BlockUtil.placeCrystalOnBlock(this.placePos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, this.placeSwing.getValue(), this.exactHand.getValue(), this.shouldSilent);
            }
        }
    }
	
	@SubscribeEvent(priority=EventPriority.HIGH, receiveCanceled=true)
    public void onSoundPacket(PacketEvent.Receive event) {
        SPacketSoundEffect packet2;
        if (AutoCrystal.fullNullCheck()) {
            return;
        }
        if (event.getPacket() instanceof SPacketSoundEffect && this.timing.getValue() == Timing.Sequential && (packet2 = (SPacketSoundEffect)event.getPacket()).getCategory() == SoundCategory.BLOCKS && packet2.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
            ArrayList entities = new ArrayList(AutoCrystal.mc.world.loadedEntityList);
            int size = entities.size();
            for (int i = 0; i < size; ++i) {
                Entity entity = (Entity)entities.get(i);
                if (!(entity instanceof EntityEnderCrystal) || !(entity.getDistanceSq(packet2.getX(), packet2.getY(), packet2.getZ()) < 36.0)) continue;
                entity.setDead();
            }
        }
    }
	
    @SubscribeEvent(priority=EventPriority.HIGH, receiveCanceled=true)
    public void onPacketReceive(PacketEvent.Receive event) {
        SPacketSoundEffect packet;
        if (AutoCrystal.fullNullCheck()) {
            return;
        }
        if (!this.justRender.getValue().booleanValue() && this.switchTimer.passedMs(this.switchCooldown.getValue().intValue()) && this.explode.getValue().booleanValue() && this.instant.getValue().booleanValue() && event.getPacket() instanceof SPacketSpawnObject && (this.syncedCrystalPos == null || !this.syncedFeetPlace.getValue().booleanValue() || this.damageSync.getValue() == DamageSync.NONE)) {
            BlockPos pos;
            SPacketSpawnObject packet2 = (SPacketSpawnObject)event.getPacket();
            if (packet2.getType() == 51 && AutoCrystal.mc.player.getDistanceSq(pos = new BlockPos(packet2.getX(), packet2.getY(), packet2.getZ())) + (double)this.predictOffset.getValue().floatValue() <= MathUtil.square(this.breakRange.getValue().floatValue()) && (this.instantTimer.getValue() == PredictTimer.NONE || this.instantTimer.getValue() == PredictTimer.BREAK && this.breakTimer.passedMs(this.breakDelay.getValue().intValue()) || this.instantTimer.getValue() == PredictTimer.PREDICT && this.predictTimer.passedMs(this.predictDelay.getValue().intValue()))) {
                if (this.predictSlowBreak(pos.down())) {
                    return;
                }
                if (this.predictFriendDmg.getValue().booleanValue() && (this.antiFriendPop.getValue() == AntiFriendPop.BREAK || this.antiFriendPop.getValue() == AntiFriendPop.ALL) && this.isRightThread()) {
                    for (EntityPlayer friend : AutoCrystal.mc.world.playerEntities) {
                        if (friend == null || AutoCrystal.mc.player.equals((Object)friend) || friend.getDistanceSq(pos) > MathUtil.square(this.range.getValue().floatValue() + this.placeRange.getValue().floatValue()) || !Experium.friendManager.isFriend(friend) || !((double)DamageUtil.calculateDamage(pos, (Entity)friend) > (double)EntityUtil.getHealth((Entity)friend) + 0.5)) continue;
                        return;
                    }
                }
                if (placedPos.contains((Object)pos.down())) {
                    float selfDamage;
                    if (this.isRightThread() && this.superSafe.getValue() != false ? DamageUtil.canTakeDamage(this.suicide.getValue()) && ((double)(selfDamage = DamageUtil.calculateDamage(pos, (Entity)AutoCrystal.mc.player)) - 0.5 > (double)EntityUtil.getHealth((Entity)AutoCrystal.mc.player) || selfDamage > this.maxSelfBreak.getValue().floatValue()) : this.superSafe.getValue() != false) {
                        return;
                    }
                    this.attackCrystalPredict(packet2.getEntityID(), pos);
                } else if (this.predictCalc.getValue().booleanValue() && this.isRightThread()) {
                    float selfDamage = -1.0f;
                    if (DamageUtil.canTakeDamage(this.suicide.getValue())) {
                        selfDamage = DamageUtil.calculateDamage(pos, (Entity)AutoCrystal.mc.player);
                    }
                    if ((double)selfDamage + 0.5 < (double)EntityUtil.getHealth((Entity)AutoCrystal.mc.player) && selfDamage <= this.maxSelfBreak.getValue().floatValue()) {
                        for (EntityPlayer player : AutoCrystal.mc.world.playerEntities) {
                            float damage;
                            if (!(player.getDistanceSq(pos) <= MathUtil.square(this.range.getValue().floatValue())) || !EntityUtil.isValid((Entity)player, this.range.getValue().floatValue() + this.breakRange.getValue().floatValue()) || this.antiNaked.getValue().booleanValue() && DamageUtil.isNaked(player) || !((damage = DamageUtil.calculateDamage(pos, (Entity)player)) > selfDamage || damage > this.minDamage.getValue().floatValue() && !DamageUtil.canTakeDamage(this.suicide.getValue())) && !(damage > EntityUtil.getHealth((Entity)player))) continue;
                            if (this.predictRotate.getValue().booleanValue() && this.eventMode.getValue() != 2 && (this.rotate.getValue() == Rotate.BREAK || this.rotate.getValue() == Rotate.ALL)) {
                                this.rotateToPos(pos);
                            }
                            this.attackCrystalPredict(packet2.getEntityID(), pos);
                            break;
                        }
                    }
                }
            }
        } else if (!this.soundConfirm.getValue().booleanValue() && event.getPacket() instanceof SPacketExplosion) {
            SPacketExplosion packet3 = (SPacketExplosion)event.getPacket();
            BlockPos pos = new BlockPos(packet3.getX(), packet3.getY(), packet3.getZ()).down();
            this.removePos(pos);
        } else if (event.getPacket() instanceof SPacketDestroyEntities) {
            SPacketDestroyEntities packet4 = (SPacketDestroyEntities)event.getPacket();
            for (int id : packet4.getEntityIDs()) {
                Entity entity = AutoCrystal.mc.world.getEntityByID(id);
                if (!(entity instanceof EntityEnderCrystal)) continue;
                brokenPos.remove((Object)new BlockPos(entity.getPositionVector()).down());
                placedPos.remove((Object)new BlockPos(entity.getPositionVector()).down());
            }
        } else if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet5 = (SPacketEntityStatus)event.getPacket();
            if (packet5.getOpCode() == 35 && packet5.getEntity((World)AutoCrystal.mc.world) instanceof EntityPlayer) {
                this.totemPops.put((EntityPlayer)packet5.getEntity((World)AutoCrystal.mc.world), new Timer().reset());
            }
        } else if (event.getPacket() instanceof SPacketSoundEffect && (packet = (SPacketSoundEffect)event.getPacket()).getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
            BlockPos pos = new BlockPos(packet.getX(), packet.getY(), packet.getZ());
            if (this.sound.getValue().booleanValue() || this.threadMode.getValue() == ThreadMode.SOUND) {
                if (AutoCrystal.fullNullCheck()) {
                    return;
                }
                NoSoundLag.removeEntities(packet, this.soundRange.getValue().floatValue());
            }
            if (this.soundConfirm.getValue().booleanValue()) {
                this.removePos(pos);
            }
            if (this.threadMode.getValue() == ThreadMode.SOUND && this.isRightThread() && AutoCrystal.mc.player != null && AutoCrystal.mc.player.getDistanceSq(pos) < MathUtil.square(this.soundPlayer.getValue().floatValue())) {
                this.handlePool(true);
            }
        }
    }

    private boolean predictSlowBreak(BlockPos pos) {
        if (this.antiCommit.getValue().booleanValue() && lowDmgPos.remove((Object)pos)) {
            return this.shouldSlowBreak(false);
        }
        return false;
    }

    private boolean isRightThread() {
        return mc.isCallingFromMinecraftThread() || !Experium.eventManager.ticksOngoing() && !this.threadOngoing.get();
    }

    private void attackCrystalPredict(int entityID, BlockPos pos) {
        if (!(!this.predictRotate.getValue().booleanValue() || this.eventMode.getValue() == 2 && this.threadMode.getValue() == ThreadMode.NONE || this.rotate.getValue() != Rotate.BREAK && this.rotate.getValue() != Rotate.ALL)) {
            this.rotateToPos(pos);
        }
        CPacketUseEntity attackPacket = new CPacketUseEntity();
        attackPacket.entityId = entityID;
        attackPacket.action = CPacketUseEntity.Action.ATTACK;
        AutoCrystal.mc.player.connection.sendPacket((Packet)attackPacket);
        if (this.breakSwing.getValue().booleanValue()) {
            AutoCrystal.mc.player.connection.sendPacket((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
        }
        if (this.resetBreakTimer.getValue().booleanValue()) {
            this.breakTimer.reset();
        }
        this.predictTimer.reset();
    }

    private void removePos(BlockPos pos) {
        if (this.damageSync.getValue() == DamageSync.PLACE) {
            if (placedPos.remove((Object)pos)) {
                this.posConfirmed = true;
            }
        } else if (this.damageSync.getValue() == DamageSync.BREAK && brokenPos.remove((Object)pos)) {
            this.posConfirmed = true;
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (!this.render.getValue().booleanValue()) {
            return;
        }
        Color boxC = new Color(this.bRed.getValue(), this.bGreen.getValue(), this.bBlue.getValue(), this.bAlpha.getValue());
        Color outlineC = new Color(this.oRed.getValue(), this.oGreen.getValue(), this.oBlue.getValue(), this.oAlpha.getValue());
        if ((this.offHand || this.mainHand || this.switchMode.getValue() == Switch.CALC) && this.renderPos != null && (this.box.getValue().booleanValue() || this.outline.getValue().booleanValue())) {
            if (this.renderMode.getValue() == RenderMode.FADE) {
                this.positions.removeIf(pos -> pos.getPos().equals((Object)this.renderPos));
                this.positions.add(new RenderPos(this.renderPos, 0.0f));
            }
            if (this.renderMode.getValue() == RenderMode.STATIC) {
                RenderUtil.drawSexyBox(new AxisAlignedBB(this.renderPos), boxC, outlineC, this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.colorSync.getValue(), 1.0f, 1.0f, this.slabHeight.getValue().floatValue());
            }
			if (this.renderMode.getValue() == RenderMode.GRADIENTRAINBOW) {
                AxisAlignedBB axisAlignedBB = AutoCrystal.mc.world.getBlockState(this.renderPos).getSelectedBoundingBox((World)AutoCrystal.mc.world, this.renderPos);
                Vec3d vec3d = EntityUtil.interpolateEntity((Entity)RenderUtil.mc.player, mc.getRenderPartialTicks());
                for (EnumFacing enumFacing : EnumFacing.values()) {
                    RenderUtil.drawGradientPlaneBB(axisAlignedBB.grow((double)0.002f).offset(-vec3d.x, -vec3d.y, -vec3d.z), enumFacing, new Color(ColorUtil.gradientRainbow(50).getRed(), ColorUtil.gradientRainbow(50).getGreen(), ColorUtil.gradientRainbow(50).getBlue(), 127), ColorUtil.invert(new Color(ColorUtil.gradientRainbow(50).getRed(), ColorUtil.gradientRainbow(50).getGreen(), ColorUtil.gradientRainbow(50).getBlue(), 127)), 2.0);
                }
                RenderUtil.drawGradientBlockOutline(axisAlignedBB.grow((double)0.002f).offset(-vec3d.x, -vec3d.y, -vec3d.z), ColorUtil.invert(new Color(ColorUtil.gradientRainbow(50).getRed(), ColorUtil.gradientRainbow(50).getGreen(), ColorUtil.gradientRainbow(50).getBlue(), 255)), new Color(ColorUtil.gradientRainbow(50).getRed(), ColorUtil.gradientRainbow(50).getGreen(), ColorUtil.gradientRainbow(50).getBlue(), 255), 2.0f);
            }
            if (this.renderMode.getValue() == RenderMode.GLIDE) {
                if (this.lastRenderPos == null || AutoCrystal.mc.player.getDistance(this.renderBB.minX, this.renderBB.minY, this.renderBB.minZ) > (double)this.range.getValue().floatValue()) {
                    this.lastRenderPos = this.renderPos;
                    this.renderBB = new AxisAlignedBB(this.renderPos);
                    this.timePassed = 0.0f;
                }
                if (!this.lastRenderPos.equals((Object)this.renderPos)) {
                    this.lastRenderPos = this.renderPos;
                    this.timePassed = 0.0f;
                }
                double xDiff = (double)this.renderPos.getX() - this.renderBB.minX;
                double yDiff = (double)this.renderPos.getY() - this.renderBB.minY;
                double zDiff = (double)this.renderPos.getZ() - this.renderBB.minZ;
                float multiplier = this.timePassed / this.moveSpeed.getValue().floatValue() * this.accel.getValue().floatValue();
                if (multiplier > 1.0f) {
                    multiplier = 1.0f;
                }
                this.renderBB = this.renderBB.offset(xDiff * (double)multiplier, yDiff * (double)multiplier, zDiff * (double)multiplier);
                RenderUtil.drawSexyBox(this.renderBB, boxC, outlineC, this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.colorSync.getValue(), 1.0f, 1.0f, this.slabHeight.getValue().floatValue());
                if (this.text.getValue().booleanValue()) {
                    RenderUtil.drawText(this.renderBB.offset(0.0, (double)(1.0f - this.slabHeight.getValue().floatValue() / 2.0f) - 0.4, 0.0), (Math.floor(this.renderDamage) == this.renderDamage ? Integer.valueOf((int)this.renderDamage) : String.format("%.1f", this.renderDamage)) + "");
                }
                float f = this.timePassed = this.renderBB.equals((Object)new AxisAlignedBB(this.renderPos)) ? 0.0f : (this.timePassed = this.timePassed + 50.0f);
            }
        }
        if (this.renderMode.getValue() == RenderMode.FADE) {
            this.positions.forEach(pos -> {
                float factor = (this.duration.getValue().floatValue() - pos.getRenderTime()) / this.duration.getValue().floatValue();
                RenderUtil.drawSexyBox(new AxisAlignedBB(pos.getPos()), boxC, outlineC, this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.colorSync.getValue(), this.fadeFactor.getValue() != false ? factor : 1.0f, this.scaleFactor.getValue() != false ? factor : 1.0f, this.slabFactor.getValue() != false ? factor : 1.0f);
                pos.setRenderTime(pos.getRenderTime() + 50.0f);
            });
            this.positions.removeIf(pos -> pos.getRenderTime() >= this.duration.getValue().floatValue() || AutoCrystal.mc.world.isAirBlock(pos.getPos()) || !AutoCrystal.mc.world.isAirBlock(pos.getPos().offset(EnumFacing.UP)));
            if (this.positions.size() > this.max.getValue()) {
                this.positions.remove(0);
            }
        }
        if ((this.offHand || this.mainHand || this.switchMode.getValue() == Switch.CALC) && this.renderPos != null && this.text.getValue().booleanValue() && this.renderMode.getValue() != RenderMode.GLIDE) {
            RenderUtil.drawText(new AxisAlignedBB(this.renderPos).offset(0.0, this.renderMode.getValue() != RenderMode.FADE ? (double)(1.0f - this.slabHeight.getValue().floatValue() / 2.0f) - 0.4 : 0.1, 0.0), (Math.floor(this.renderDamage) == this.renderDamage ? Integer.valueOf((int)this.renderDamage) : String.format("%.1f", this.renderDamage)) + "");
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKeyState() && !(AutoCrystal.mc.currentScreen instanceof ExperiumGui) && this.switchBind.getValue().getKey() == Keyboard.getEventKey()) {
            if (this.switchBack.getValue().booleanValue() && this.offhandSwitch.getValue().booleanValue() && this.offHand) {
                Offhand module = Experium.moduleManager.getModuleByClass(Offhand.class);
                if (module.isOff()) {
                    Command.sendMessage("<" + this.getDisplayName() + "> \u00a7cSwitch failed. Enable the Offhand module.");
                } else if (module.type.getValue() == Offhand.Type.NEW) {
                    module.setSwapToTotem(true);
                    module.doOffhand();
                } else {
                    module.setMode(Offhand.Mode2.TOTEMS);
                    module.doSwitch();
                }
                return;
            }
            this.switching = !this.switching;
        }
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting() != null && event.getSetting().getFeature() != null && event.getSetting().getFeature().equals(this) && this.isEnabled() && (event.getSetting().equals(this.threadDelay) || event.getSetting().equals(this.threadMode))) {
            if (this.executor != null) {
                this.executor.shutdown();
            }
            if (this.thread != null) {
                this.shouldInterrupt.set(true);
            }
        }
    }

    private void postProcessing() {
        if (this.threadMode.getValue() != ThreadMode.NONE || this.eventMode.getValue() != 2 || this.rotate.getValue() == Rotate.OFF || !this.rotateFirst.getValue().booleanValue()) {
            return;
        }
        switch (this.logic.getValue()) {
            case BREAKPLACE: {
                this.postProcessBreak();
                this.postProcessPlace();
                break;
            }
            case PLACEBREAK: {
                this.postProcessPlace();
                this.postProcessBreak();
            }
        }
    }

    private void postProcessBreak() {
        while (!this.packetUseEntities.isEmpty()) {
            CPacketUseEntity packet = this.packetUseEntities.poll();
            AutoCrystal.mc.player.connection.sendPacket((Packet)packet);
            if (this.breakSwing.getValue().booleanValue()) {
                AutoCrystal.mc.player.swingArm(EnumHand.MAIN_HAND);
            }
            this.breakTimer.reset();
        }
    }

    private void postProcessPlace() {
        if (this.placeInfo != null) {
            this.placeInfo.runPlace();
            this.placeTimer.reset();
            this.placeInfo = null;
        }
    }

    private void processMultiThreading() {
        if (this.isOff()) {
            return;
        }
        if (this.threadMode.getValue() == ThreadMode.WHILE) {
            this.handleWhile();
        } else if (this.threadMode.getValue() != ThreadMode.NONE) {
            this.handlePool(false);
        }
    }

    private void handlePool(boolean justDoIt) {
        if (justDoIt || this.executor == null || this.executor.isTerminated() || this.executor.isShutdown() || this.syncroTimer.passedMs(this.syncThreads.getValue().intValue()) && this.syncThreadBool.getValue().booleanValue()) {
            if (this.executor != null) {
                this.executor.shutdown();
            }
            this.executor = this.getExecutor();
            this.syncroTimer.reset();
        }
    }

    private void handleWhile() {
        if (this.thread == null || this.thread.isInterrupted() || !this.thread.isAlive() || this.syncroTimer.passedMs(this.syncThreads.getValue().intValue()) && this.syncThreadBool.getValue().booleanValue()) {
            if (this.thread == null) {
                this.thread = new Thread(RAutoCrystal.getInstance(this));
            } else if (this.syncroTimer.passedMs(this.syncThreads.getValue().intValue()) && !this.shouldInterrupt.get() && this.syncThreadBool.getValue().booleanValue()) {
                this.shouldInterrupt.set(true);
                this.syncroTimer.reset();
                return;
            }
            if (this.thread != null && (this.thread.isInterrupted() || !this.thread.isAlive())) {
                this.thread = new Thread(RAutoCrystal.getInstance(this));
            }
            if (this.thread != null && this.thread.getState() == Thread.State.NEW) {
                try {
                    this.thread.start();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                this.syncroTimer.reset();
            }
        }
    }

    private ScheduledExecutorService getExecutor() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(RAutoCrystal.getInstance(this), 0L, this.threadDelay.getValue().intValue(), TimeUnit.MILLISECONDS);
        return service;
    }

    public void doAutoCrystal() {
        if (this.check()) {
            switch (this.logic.getValue()) {
                case PLACEBREAK: {
                    this.placeCrystal();
                    this.breakCrystal();
                    break;
                }
                case BREAKPLACE: {
                    this.breakCrystal();
                    this.placeCrystal();
                }
            }
            this.manualBreaker();
        }
    }

    private boolean check() {
        if (AutoCrystal.fullNullCheck()) {
            return false;
        }
        if (this.syncTimer.passedMs(this.damageSyncTime.getValue().intValue())) {
            this.currentSyncTarget = null;
            this.syncedCrystalPos = null;
            this.syncedPlayerPos = null;
        } else if (this.syncySync.getValue().booleanValue() && this.syncedCrystalPos != null) {
            this.posConfirmed = true;
        }
        this.foundDoublePop = false;
        if (this.renderTimer.passedMs(500L)) {
            this.renderPos = null;
            this.renderTimer.reset();
        }
        this.mainHand = AutoCrystal.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL;
        boolean bl = this.mainHand;
        if (this.autoSwitch.getValue() == AutoSwitch.SILENT && InventoryUtil.getItemHotbar(Items.END_CRYSTAL) != -1) {
            this.mainHand = true;
            this.shouldSilent = true;
        } else {
            this.shouldSilent = false;
        }
        this.offHand = AutoCrystal.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
        this.currentDamage = 0.0;
        this.placePos = null;
        if (this.lastSlot != AutoCrystal.mc.player.inventory.currentItem || AutoTrap.isPlacing || Surround.isPlacing) {
            this.lastSlot = AutoCrystal.mc.player.inventory.currentItem;
            this.switchTimer.reset();
        }
        if (!this.offHand && !this.mainHand) {
            this.placeInfo = null;
            this.packetUseEntities.clear();
        }
        if (this.offHand || this.mainHand) {
            this.switching = false;
        }
        if (!((this.offHand || this.mainHand || this.switchMode.getValue() != Switch.BREAKSLOT || this.switching) && DamageUtil.canBreakWeakness((EntityPlayer)AutoCrystal.mc.player) && this.switchTimer.passedMs(this.switchCooldown.getValue().intValue()))) {
            this.renderPos = null;
            target = null;
            this.rotating = false;
            return false;
        }
        if (this.mineSwitch.getValue().booleanValue() && Mouse.isButtonDown((int)0) && (this.switching || this.autoSwitch.getValue() == AutoSwitch.ALWAYS) && Mouse.isButtonDown((int)1) && AutoCrystal.mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe) {
            this.switchItem();
        }
        this.mapCrystals();
        if (!this.posConfirmed && this.damageSync.getValue() != DamageSync.NONE && this.syncTimer.passedMs(this.confirm.getValue().intValue())) {
            this.syncTimer.setMs(this.damageSyncTime.getValue() + 1);
        }
        return true;
    }

    private void mapCrystals() {
        this.efficientTarget = null;
        if (this.packets.getValue() != 1) {
            this.attackList = new ConcurrentLinkedQueue<Entity>();
            this.crystalMap = new HashMap<Entity, Float>();
        }
        this.crystalCount = 0;
        this.minDmgCount = 0;
        Entity maxCrystal = null;
        float maxDamage = 0.5f;
        for (Entity entity : AutoCrystal.mc.world.loadedEntityList) {
            if (entity.isDead || !(entity instanceof EntityEnderCrystal) || !this.isValid(entity)) continue;
            if (this.syncedFeetPlace.getValue().booleanValue() && entity.getPosition().down().equals((Object)this.syncedCrystalPos) && this.damageSync.getValue() != DamageSync.NONE) {
                ++this.minDmgCount;
                ++this.crystalCount;
                if (this.syncCount.getValue().booleanValue()) {
                    this.minDmgCount = this.wasteAmount.getValue() + 1;
                    this.crystalCount = this.wasteAmount.getValue() + 1;
                }
                if (!this.hyperSync.getValue().booleanValue()) continue;
                maxCrystal = null;
                break;
            }
            boolean count = false;
            boolean countMin = false;
            float selfDamage = -1.0f;
            if (DamageUtil.canTakeDamage(this.suicide.getValue())) {
                selfDamage = DamageUtil.calculateDamage(entity, (Entity)AutoCrystal.mc.player);
            }
            if ((double)selfDamage + 0.5 < (double)EntityUtil.getHealth((Entity)AutoCrystal.mc.player) && selfDamage <= this.maxSelfBreak.getValue().floatValue()) {
                Entity beforeCrystal = maxCrystal;
                float beforeDamage = maxDamage;
                for (EntityPlayer player : AutoCrystal.mc.world.playerEntities) {
                    if (!(player.getDistanceSq(entity) <= MathUtil.square(this.range.getValue().floatValue()))) continue;
                    if (EntityUtil.isValid((Entity)player, this.range.getValue().floatValue() + this.breakRange.getValue().floatValue())) {
                        float damage;
                        if (this.antiNaked.getValue().booleanValue() && DamageUtil.isNaked(player) || !((damage = DamageUtil.calculateDamage(entity, (Entity)player)) > selfDamage || damage > this.minDamage.getValue().floatValue() && !DamageUtil.canTakeDamage(this.suicide.getValue())) && !(damage > EntityUtil.getHealth((Entity)player))) continue;
                        if (damage > maxDamage) {
                            maxDamage = damage;
                            maxCrystal = entity;
                        }
                        if (this.packets.getValue() == 1) {
                            if (damage >= this.minDamage.getValue().floatValue() || !this.wasteMinDmgCount.getValue().booleanValue()) {
                                count = true;
                            }
                            countMin = true;
                            continue;
                        }
                        if (this.crystalMap.get((Object)entity) != null && !(this.crystalMap.get((Object)entity).floatValue() < damage)) continue;
                        this.crystalMap.put(entity, Float.valueOf(damage));
                        continue;
                    }
                    if (this.antiFriendPop.getValue() != AntiFriendPop.BREAK && this.antiFriendPop.getValue() != AntiFriendPop.ALL || !Experium.friendManager.isFriend(player.getName()) || !((double)DamageUtil.calculateDamage(entity, (Entity)player) > (double)EntityUtil.getHealth((Entity)player) + 0.5)) continue;
                    maxCrystal = beforeCrystal;
                    maxDamage = beforeDamage;
                    this.crystalMap.remove((Object)entity);
                    if (!this.noCount.getValue().booleanValue()) break;
                    count = false;
                    countMin = false;
                    break;
                }
            }
            if (!countMin) continue;
            ++this.minDmgCount;
            if (!count) continue;
            ++this.crystalCount;
        }
        if (this.damageSync.getValue() == DamageSync.BREAK && ((double)maxDamage > this.lastDamage || this.syncTimer.passedMs(this.damageSyncTime.getValue().intValue()) || this.damageSync.getValue() == DamageSync.NONE)) {
            this.lastDamage = maxDamage;
        }
        if (this.enormousSync.getValue().booleanValue() && this.syncedFeetPlace.getValue().booleanValue() && this.damageSync.getValue() != DamageSync.NONE && this.syncedCrystalPos != null) {
            if (this.syncCount.getValue().booleanValue()) {
                this.minDmgCount = this.wasteAmount.getValue() + 1;
                this.crystalCount = this.wasteAmount.getValue() + 1;
            }
            return;
        }
        if (this.webAttack.getValue().booleanValue() && this.webPos != null) {
            if (AutoCrystal.mc.player.getDistanceSq(this.webPos.up()) > MathUtil.square(this.breakRange.getValue().floatValue())) {
                this.webPos = null;
            } else {
                for (Entity entity : AutoCrystal.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(this.webPos.up()))) {
                    if (!(entity instanceof EntityEnderCrystal)) continue;
                    this.attackList.add(entity);
                    this.efficientTarget = entity;
                    this.webPos = null;
                    this.lastDamage = 0.5;
                    return;
                }
            }
        }
        if (this.shouldSlowBreak(true) && maxDamage < this.minDamage.getValue().floatValue() && (target == null || !(EntityUtil.getHealth((Entity)target) <= this.facePlace.getValue().floatValue()) || !this.breakTimer.passedMs(this.facePlaceSpeed.getValue().intValue()) && this.slowFaceBreak.getValue().booleanValue() && Mouse.isButtonDown((int)0) && this.holdFacePlace.getValue().booleanValue() && this.holdFaceBreak.getValue().booleanValue())) {
            this.efficientTarget = null;
            return;
        }
        if (this.packets.getValue() == 1) {
            this.efficientTarget = maxCrystal;
        } else {
            this.crystalMap = MathUtil.sortByValue(this.crystalMap, true);
            for (Map.Entry entry : this.crystalMap.entrySet()) {
                Entity crystal = (Entity)entry.getKey();
                float damage = ((Float)entry.getValue()).floatValue();
                if (damage >= this.minDamage.getValue().floatValue() || !this.wasteMinDmgCount.getValue().booleanValue()) {
                    ++this.crystalCount;
                }
                this.attackList.add(crystal);
                ++this.minDmgCount;
            }
        }
    }

    private boolean shouldSlowBreak(boolean withManual) {
        return withManual && this.manual.getValue() != false && this.manualMinDmg.getValue() != false && Mouse.isButtonDown((int)1) && (!Mouse.isButtonDown((int)0) || this.holdFacePlace.getValue() == false) || this.holdFacePlace.getValue() != false && this.holdFaceBreak.getValue() != false && Mouse.isButtonDown((int)0) && !this.breakTimer.passedMs(this.facePlaceSpeed.getValue().intValue()) || this.slowFaceBreak.getValue() != false && !this.breakTimer.passedMs(this.facePlaceSpeed.getValue().intValue());
    }

    private void placeCrystal() {
        int crystalLimit = this.wasteAmount.getValue();
        if (this.placeTimer.passedMs(this.placeDelay.getValue().intValue()) && this.place.getValue().booleanValue() && (this.offHand || this.mainHand || this.switchMode.getValue() == Switch.CALC || this.switchMode.getValue() == Switch.BREAKSLOT && this.switching)) {
            if (!(!this.offHand && !this.mainHand && (this.switchMode.getValue() == Switch.ALWAYS || this.switching) || this.crystalCount < crystalLimit || this.antiSurround.getValue().booleanValue() && this.lastPos != null && this.lastPos.equals((Object)this.placePos))) {
                return;
            }
            this.calculateDamage(this.getTarget(this.targetMode.getValue() == Target.UNSAFE));
            if (target != null && this.placePos != null) {
                if (!this.offHand && !this.mainHand && this.autoSwitch.getValue() != AutoSwitch.NONE && (this.currentDamage > (double)this.minDamage.getValue().floatValue() || this.lethalSwitch.getValue().booleanValue() && EntityUtil.getHealth((Entity)target) <= this.facePlace.getValue().floatValue()) && !this.switchItem()) {
                    return;
                }
                if (this.currentDamage < (double)this.minDamage.getValue().floatValue() && this.limitFacePlace.getValue().booleanValue()) {
                    crystalLimit = 1;
                }
                if (this.currentDamage >= (double)this.minMinDmg.getValue().floatValue() && (this.offHand || this.mainHand || this.autoSwitch.getValue() != AutoSwitch.NONE) && (this.crystalCount < crystalLimit || this.antiSurround.getValue().booleanValue() && this.lastPos != null && this.lastPos.equals((Object)this.placePos)) && (this.currentDamage > (double)this.minDamage.getValue().floatValue() || this.minDmgCount < crystalLimit) && this.currentDamage >= 1.0 && (DamageUtil.isArmorLow(target, this.minArmor.getValue()) || EntityUtil.getHealth((Entity)target) <= this.facePlace.getValue().floatValue() || this.currentDamage > (double)this.minDamage.getValue().floatValue() || this.shouldHoldFacePlace())) {
                    float damageOffset = this.damageSync.getValue() == DamageSync.BREAK ? this.dropOff.getValue().floatValue() - 5.0f : 0.0f;
                    boolean syncflag = false;
                    if (this.syncedFeetPlace.getValue().booleanValue() && this.placePos.equals((Object)this.lastPos) && this.isEligableForFeetSync(target, this.placePos) && !this.syncTimer.passedMs(this.damageSyncTime.getValue().intValue()) && target.equals((Object)this.currentSyncTarget) && target.getPosition().equals((Object)this.syncedPlayerPos) && this.damageSync.getValue() != DamageSync.NONE) {
                        this.syncedCrystalPos = this.placePos;
                        this.lastDamage = this.currentDamage;
                        if (this.fullSync.getValue().booleanValue()) {
                            this.lastDamage = 100.0;
                        }
                        syncflag = true;
                    }
                    if (syncflag || this.currentDamage - (double)damageOffset > this.lastDamage || this.syncTimer.passedMs(this.damageSyncTime.getValue().intValue()) || this.damageSync.getValue() == DamageSync.NONE) {
                        if (!syncflag && this.damageSync.getValue() != DamageSync.BREAK) {
                            this.lastDamage = this.currentDamage;
                        }
                        if (!this.onlyplaced.getValue().booleanValue()) {
                            this.renderPos = this.placePos;
                        }
                        this.renderDamage = this.currentDamage;
                        if (this.switchItem()) {
                            this.currentSyncTarget = target;
                            this.syncedPlayerPos = target.getPosition();
                            if (this.foundDoublePop) {
                                this.totemPops.put(target, new Timer().reset());
                            }
                            this.rotateToPos(this.placePos);
                            if (this.addTolowDmg || this.actualSlowBreak.getValue().booleanValue() && this.currentDamage < (double)this.minDamage.getValue().floatValue()) {
                                lowDmgPos.add(this.placePos);
                            }
                            placedPos.add(this.placePos);
                            if (!this.justRender.getValue().booleanValue()) {
                                if (this.eventMode.getValue() == 2 && this.threadMode.getValue() == ThreadMode.NONE && this.rotateFirst.getValue().booleanValue() && this.rotate.getValue() != Rotate.OFF) {
                                    this.placeInfo = new PlaceInfo(this.placePos, this.offHand, this.placeSwing.getValue(), this.exactHand.getValue(), this.shouldSilent);
                                } else {
                                    BlockUtil.placeCrystalOnBlock(this.placePos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, this.placeSwing.getValue(), this.exactHand.getValue(), this.shouldSilent);
                                }
                            }
                            this.lastPos = this.placePos;
                            this.placeTimer.reset();
                            this.posConfirmed = false;
                            if (this.syncTimer.passedMs(this.damageSyncTime.getValue().intValue())) {
                                this.syncedCrystalPos = null;
                                this.syncTimer.reset();
                            }
                        }
                    }
                }
            } else {
                this.renderPos = null;
            }
        }
    }

    private boolean shouldHoldFacePlace() {
        this.addTolowDmg = false;
        if (this.holdFacePlace.getValue().booleanValue() && Mouse.isButtonDown((int)0)) {
            this.addTolowDmg = true;
            return true;
        }
        return false;
    }

    private boolean switchItem() {
        if (this.offHand || this.mainHand) {
            return true;
        }
        switch (this.autoSwitch.getValue()) {
            case NONE: {
                return false;
            }
            case TOGGLE: {
                if (!this.switching) {
                    return false;
                }
            }
            case ALWAYS: {
                if (!this.doSwitch()) break;
                return true;
            }
        }
        return false;
    }

    private boolean doSwitch() {
        if (this.offhandSwitch.getValue().booleanValue()) {
            Offhand module = Experium.moduleManager.getModuleByClass(Offhand.class);
            if (module.isOff()) {
                Command.sendMessage("<" + this.getDisplayName() + "> \u00a7cSwitch failed. Enable the Offhand module.");
                this.switching = false;
                return false;
            }
            if (module.type.getValue() == Offhand.Type.NEW) {
                module.setSwapToTotem(false);
                module.setMode(Offhand.Mode.CRYSTALS);
                module.doOffhand();
            } else {
                module.setMode(Offhand.Mode2.CRYSTALS);
                module.doSwitch();
            }
            this.switching = false;
            return true;
        }
        if (AutoCrystal.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            this.mainHand = false;
        } else {
            InventoryUtil.switchToHotbarSlot(ItemEndCrystal.class, false);
            this.mainHand = true;
        }
        this.switching = false;
        return true;
    }

    private void calculateDamage(EntityPlayer targettedPlayer) {
        BlockPos playerPos;
        if (targettedPlayer == null && this.targetMode.getValue() != Target.DAMAGE && !this.fullCalc.getValue().booleanValue()) {
            return;
        }
        float maxDamage = 0.5f;
        EntityPlayer currentTarget = null;
        BlockPos currentPos = null;
        float maxSelfDamage = 0.0f;
        this.foundDoublePop = false;
        BlockPos setToAir = null;
        IBlockState state = null;
        if (this.webAttack.getValue().booleanValue() && targettedPlayer != null && AutoCrystal.mc.world.getBlockState(playerPos = new BlockPos(targettedPlayer.getPositionVector())).getBlock() == Blocks.WEB) {
            setToAir = playerPos;
            state = AutoCrystal.mc.world.getBlockState(playerPos);
            AutoCrystal.mc.world.setBlockToAir(playerPos);
        }
        block0: for (BlockPos pos : BlockUtil.possiblePlacePositions(this.placeRange.getValue().floatValue(), this.antiSurround.getValue(), this.oneDot15.getValue())) {
            if (!BlockUtil.rayTracePlaceCheck(pos, (this.raytrace.getValue() == Raytrace.PLACE || this.raytrace.getValue() == Raytrace.FULL) && AutoCrystal.mc.player.getDistanceSq(pos) > MathUtil.square(this.placetrace.getValue().floatValue()), 1.0f)) continue;
            float selfDamage = -1.0f;
            if (DamageUtil.canTakeDamage(this.suicide.getValue())) {
                selfDamage = DamageUtil.calculateDamage(pos, (Entity)AutoCrystal.mc.player);
            }
            if (!((double)selfDamage + 0.5 < (double)EntityUtil.getHealth((Entity)AutoCrystal.mc.player)) || !(selfDamage <= this.maxSelfPlace.getValue().floatValue())) continue;
            if (targettedPlayer != null) {
                float playerDamage = DamageUtil.calculateDamage(pos, (Entity)targettedPlayer);
                if (this.calcEvenIfNoDamage.getValue().booleanValue() && (this.antiFriendPop.getValue() == AntiFriendPop.ALL || this.antiFriendPop.getValue() == AntiFriendPop.PLACE)) {
                    boolean friendPop = false;
                    for (EntityPlayer friend : AutoCrystal.mc.world.playerEntities) {
                        if (friend == null || AutoCrystal.mc.player.equals((Object)friend) || friend.getDistanceSq(pos) > MathUtil.square(this.range.getValue().floatValue() + this.placeRange.getValue().floatValue()) || !Experium.friendManager.isFriend(friend) || !((double)DamageUtil.calculateDamage(pos, (Entity)friend) > (double)EntityUtil.getHealth((Entity)friend) + 0.5)) continue;
                        friendPop = true;
                        break;
                    }
                    if (friendPop) continue;
                }
                if (this.isDoublePoppable(targettedPlayer, playerDamage) && (currentPos == null || targettedPlayer.getDistanceSq(pos) < targettedPlayer.getDistanceSq(currentPos))) {
                    currentTarget = targettedPlayer;
                    maxDamage = playerDamage;
                    currentPos = pos;
                    this.foundDoublePop = true;
                    continue;
                }
                if (!(!this.foundDoublePop && (playerDamage > maxDamage || this.extraSelfCalc.getValue() != false && playerDamage >= maxDamage && selfDamage < maxSelfDamage) && (playerDamage > selfDamage || playerDamage > this.minDamage.getValue().floatValue() && !DamageUtil.canTakeDamage(this.suicide.getValue()) || playerDamage > EntityUtil.getHealth((Entity)targettedPlayer)))) continue;
                maxDamage = playerDamage;
                currentTarget = targettedPlayer;
                currentPos = pos;
                maxSelfDamage = selfDamage;
                continue;
            }
            float maxDamageBefore = maxDamage;
            EntityPlayer currentTargetBefore = currentTarget;
            BlockPos currentPosBefore = currentPos;
            float maxSelfDamageBefore = maxSelfDamage;
            for (EntityPlayer player : AutoCrystal.mc.world.playerEntities) {
                if (EntityUtil.isValid((Entity)player, this.placeRange.getValue().floatValue() + this.range.getValue().floatValue())) {
                    if (this.antiNaked.getValue().booleanValue() && DamageUtil.isNaked(player)) continue;
                    float playerDamage = DamageUtil.calculateDamage(pos, (Entity)player);
                    if (this.doublePopOnDamage.getValue().booleanValue() && this.isDoublePoppable(player, playerDamage) && (currentPos == null || player.getDistanceSq(pos) < player.getDistanceSq(currentPos))) {
                        currentTarget = player;
                        maxDamage = playerDamage;
                        currentPos = pos;
                        maxSelfDamage = selfDamage;
                        this.foundDoublePop = true;
                        if (this.antiFriendPop.getValue() != AntiFriendPop.BREAK && this.antiFriendPop.getValue() != AntiFriendPop.PLACE) continue;
                        continue block0;
                    }
                    if (!(!this.foundDoublePop && (playerDamage > maxDamage || this.extraSelfCalc.getValue() != false && playerDamage >= maxDamage && selfDamage < maxSelfDamage) && (playerDamage > selfDamage || playerDamage > this.minDamage.getValue().floatValue() && !DamageUtil.canTakeDamage(this.suicide.getValue()) || playerDamage > EntityUtil.getHealth((Entity)player)))) continue;
                    maxDamage = playerDamage;
                    currentTarget = player;
                    currentPos = pos;
                    maxSelfDamage = selfDamage;
                    continue;
                }
                if (this.antiFriendPop.getValue() != AntiFriendPop.ALL && this.antiFriendPop.getValue() != AntiFriendPop.PLACE || player == null || !(player.getDistanceSq(pos) <= MathUtil.square(this.range.getValue().floatValue() + this.placeRange.getValue().floatValue())) || !Experium.friendManager.isFriend(player) || !((double)DamageUtil.calculateDamage(pos, (Entity)player) > (double)EntityUtil.getHealth((Entity)player) + 0.5)) continue;
                maxDamage = maxDamageBefore;
                currentTarget = currentTargetBefore;
                currentPos = currentPosBefore;
                maxSelfDamage = maxSelfDamageBefore;
                continue block0;
            }
        }
        if (setToAir != null) {
            AutoCrystal.mc.world.setBlockState(setToAir, state);
            this.webPos = currentPos;
        }
        target = currentTarget;
        this.currentDamage = maxDamage;
        this.placePos = currentPos;
    }

    private EntityPlayer getTarget(boolean unsafe) {
        if (this.targetMode.getValue() == Target.DAMAGE) {
            return null;
        }
        EntityPlayer currentTarget = null;
        for (EntityPlayer player : AutoCrystal.mc.world.playerEntities) {
            if (EntityUtil.isntValid((Entity)player, this.placeRange.getValue().floatValue() + this.range.getValue().floatValue()) || this.antiNaked.getValue().booleanValue() && DamageUtil.isNaked(player) || unsafe && EntityUtil.isSafe((Entity)player)) continue;
            if (this.minArmor.getValue() > 0 && DamageUtil.isArmorLow(player, this.minArmor.getValue())) {
                currentTarget = player;
                break;
            }
            if (currentTarget == null) {
                currentTarget = player;
                continue;
            }
            if (!(AutoCrystal.mc.player.getDistanceSq((Entity)player) < AutoCrystal.mc.player.getDistanceSq((Entity)currentTarget))) continue;
            currentTarget = player;
        }
        if (unsafe && currentTarget == null) {
            return this.getTarget(false);
        }
        if (this.predictPos.getValue().booleanValue() && currentTarget != null) {
            currentTarget.getUniqueID();
            GameProfile profile = new GameProfile(currentTarget.getUniqueID(), currentTarget.getName());
            EntityOtherPlayerMP newTarget = new EntityOtherPlayerMP((World)AutoCrystal.mc.world, profile);
            Vec3d extrapolatePosition = MathUtil.extrapolatePlayerPosition(currentTarget, this.predictTicks.getValue());
            newTarget.copyLocationAndAnglesFrom((Entity)currentTarget);
            newTarget.posX = extrapolatePosition.x;
            newTarget.posY = extrapolatePosition.y;
            newTarget.posZ = extrapolatePosition.z;
            newTarget.setHealth(EntityUtil.getHealth((Entity)currentTarget));
            newTarget.inventory.copyInventory(currentTarget.inventory);
            currentTarget = newTarget;
        }
        return currentTarget;
    }

    private void breakCrystal() {
		int swordSlot = InventoryUtil.findHotbarBlock(ItemSword.class);
		int oldSlot = AutoCrystal.mc.player.inventory.currentItem;
        if (this.explode.getValue().booleanValue() && this.breakTimer.passedMs(this.breakDelay.getValue().intValue()) && (this.switchMode.getValue() == Switch.ALWAYS || this.mainHand || this.offHand)) {
            if (this.packets.getValue() == 1 && this.efficientTarget != null) {
                if (this.justRender.getValue()) {
                    this.doFakeSwing();
                    return;
                }
                if (this.syncedFeetPlace.getValue().booleanValue() && this.gigaSync.getValue().booleanValue() && this.syncedCrystalPos != null && this.damageSync.getValue() != DamageSync.NONE) {
                    return;
                }
				this.rotateTo(this.efficientTarget);
                if (!this.offHand && !this.mainHand && this.switchMode.getValue() == Switch.BREAKSLOT && !this.switching || !DamageUtil.canBreakWeakness(AutoCrystal.mc.player) || !this.switchTimer.passedMs(this.switchCooldown.getValue().intValue()) && this.antiWeakness.getValue() != AntiWeaknessMode.None) {
                    if (this.antiWeakness.getValue() == AntiWeaknessMode.Silent) {
                        AutoCrystal.mc.player.connection.sendPacket(new CPacketHeldItemChange(swordSlot));
                        this.attackEntity(this.efficientTarget);
                        AutoCrystal.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
                    }
                    if (this.antiWeakness.getValue() == AntiWeaknessMode.Normal) {
                        InventoryUtil.switchToHotbarSlot(swordSlot, false);
                        this.attackEntity(this.efficientTarget);
                        InventoryUtil.switchToHotbarSlot(oldSlot, false);
                    }
                } else {
                    this.attackEntity(this.efficientTarget);
                }
                this.breakTimer.reset();
            } else if (!this.attackList.isEmpty()) {
                if (this.justRender.getValue().booleanValue()) {
                    this.doFakeSwing();
                    return;
                }
                if (this.syncedFeetPlace.getValue().booleanValue() && this.gigaSync.getValue().booleanValue() && this.syncedCrystalPos != null && this.damageSync.getValue() != DamageSync.NONE) {
                    return;
                }
                for (int i = 0; i < this.packets.getValue(); ++i) {
                    Entity entity = this.attackList.poll();
                    if (entity == null) continue;
                    this.rotateTo(entity);
                    this.attackEntity(entity);
                }
                this.breakTimer.reset();
            }
        }
    }

    private void attackEntity(Entity entity) {
        if (entity != null) {
            if (this.eventMode.getValue() == 2 && this.threadMode.getValue() == ThreadMode.NONE && this.rotateFirst.getValue().booleanValue() && this.rotate.getValue() != Rotate.OFF) {
                this.packetUseEntities.add(new CPacketUseEntity(entity));
            } else {
                EntityUtil.attackEntity(entity, this.sync.getValue(), this.breakSwing.getValue());
                EntityUtil.OffhandAttack(entity, this.attackOppositeHand.getValue(), this.attackOppositeHand.getValue());
                brokenPos.add(new BlockPos(entity.getPositionVector()).down());
            }
        }
    }

    private void doFakeSwing() {
        if (this.fakeSwing.getValue().booleanValue()) {
            EntityUtil.swingArmNoPacket(EnumHand.MAIN_HAND, (EntityLivingBase)AutoCrystal.mc.player);
        }
    }

    private void manualBreaker() {
        RayTraceResult result;
        if (this.rotate.getValue() != Rotate.OFF && this.eventMode.getValue() != 2 && this.rotating) {
            if (this.didRotation) {
                AutoCrystal.mc.player.rotationPitch = (float)((double)AutoCrystal.mc.player.rotationPitch + 4.0E-4);
                this.didRotation = false;
            } else {
                AutoCrystal.mc.player.rotationPitch = (float)((double)AutoCrystal.mc.player.rotationPitch - 4.0E-4);
                this.didRotation = true;
            }
        }
        if ((this.offHand || this.mainHand) && this.manual.getValue().booleanValue() && this.manualTimer.passedMs(this.manualBreak.getValue().intValue()) && Mouse.isButtonDown((int)1) && AutoCrystal.mc.player.getHeldItemOffhand().getItem() != Items.GOLDEN_APPLE && AutoCrystal.mc.player.inventory.getCurrentItem().getItem() != Items.GOLDEN_APPLE && AutoCrystal.mc.player.inventory.getCurrentItem().getItem() != Items.BOW && AutoCrystal.mc.player.inventory.getCurrentItem().getItem() != Items.EXPERIENCE_BOTTLE && (result = AutoCrystal.mc.objectMouseOver) != null) {
            switch (result.typeOfHit) {
                case ENTITY: {
                    Entity entity = result.entityHit;
                    if (!(entity instanceof EntityEnderCrystal)) break;
                    EntityUtil.attackEntity(entity, this.sync.getValue(), this.breakSwing.getValue());
                    EntityUtil.OffhandAttack(entity, this.attackOppositeHand.getValue(), this.attackOppositeHand.getValue());
                    this.manualTimer.reset();
                    break;
                }
                case BLOCK: {
                    BlockPos mousePos = AutoCrystal.mc.objectMouseOver.getBlockPos().up();
                    for (Entity target : AutoCrystal.mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(mousePos))) {
                        if (!(target instanceof EntityEnderCrystal)) continue;
                        EntityUtil.attackEntity(target, this.sync.getValue(), this.breakSwing.getValue());
                        EntityUtil.OffhandAttack(target, this.attackOppositeHand.getValue(), this.attackOppositeHand.getValue());
                        this.manualTimer.reset();
                    }
                    break;
                }
            }
        }
    }

    private void rotateTo(Entity entity) {
        switch (this.rotate.getValue()) {
            case OFF: {
                this.rotating = false;
            }
            case PLACE: {
                break;
            }
            case BREAK: 
            case ALL: {
                float[] angle = MathUtil.calcAngle(AutoCrystal.mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionVector());
                if (this.eventMode.getValue() == 2 && this.threadMode.getValue() == ThreadMode.NONE) {
                    if (this.YawStep.getValue().booleanValue()) {
                        float f = Experium.rotationManager.getYaw();
                        while (f < angle[1]) {
                            if (AutoCrystal.mc.player.ticksExisted % this.YawStepTicks.getValue() != 0) continue;
                            Experium.rotationManager.setPlayerRotations(f += (float)this.YawStepVal.getValue().intValue(), angle[1]);
                            if (!this.YawStepDebugMessages.getValue().booleanValue()) continue;
                            Command.sendMessage("Yaw " + Experium.rotationManager.getYaw());
                        }
                        break;
                    }
                    Experium.rotationManager.setPlayerRotations(angle[0], angle[1]);
                    break;
                }
                this.yaw = angle[0];
                this.pitch = angle[1];
                this.rotating = true;
            }
        }
    }

    private void rotateToPos(BlockPos pos) {
        switch (this.rotate.getValue()) {
            case OFF: {
                this.rotating = false;
            }
            case BREAK: {
                break;
            }
            case PLACE: 
            case ALL: {
                float[] angle = MathUtil.calcAngle(AutoCrystal.mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((double)((float)pos.getX() + 0.5f), (double)((float)pos.getY() - 0.5f), (double)((float)pos.getZ() + 0.5f)));
                if (this.eventMode.getValue() == 2 && this.threadMode.getValue() == ThreadMode.NONE) {
                    if (this.YawStep.getValue().booleanValue()) {
                        float f = Experium.rotationManager.getYaw();
                        while (f < angle[1]) {
                            if (AutoCrystal.mc.player.ticksExisted % this.YawStepTicks.getValue() != 0) continue;
                            Experium.rotationManager.setPlayerRotations(f += (float)this.YawStepVal.getValue().intValue(), angle[1]);
                            this.yawStepTimer.reset();
                            if (!this.YawStepDebugMessages.getValue().booleanValue()) continue;
                            Command.sendMessage("Yaw" + Experium.rotationManager.getYaw());
                        }
                        break;
                    }
                    Experium.rotationManager.setPlayerRotations(angle[0], angle[1]);
                    break;
                }
                this.yaw = angle[0];
                this.pitch = angle[1];
                this.rotating = true;
            }
        }
    }

    private boolean isDoublePoppable(EntityPlayer player, float damage) {
        if (this.doublePop.getValue().booleanValue()) {
            float health = EntityUtil.getHealth((Entity)player);
            if ((double)health <= this.popHealth.getValue() && (double)damage > (double)health + 0.5 && damage <= this.popDamage.getValue().floatValue()) {
                Timer timer = this.totemPops.get((Object)player);
                return timer == null || timer.passedMs(this.popTime.getValue().intValue());
            }
        }
        return false;
    }

    private boolean isValid(Entity entity) {
        return entity != null && AutoCrystal.mc.player.getDistanceSq(entity) <= MathUtil.square(this.breakRange.getValue().floatValue()) && (this.raytrace.getValue() == Raytrace.NONE || this.raytrace.getValue() == Raytrace.PLACE || AutoCrystal.mc.player.canEntityBeSeen(entity) || !AutoCrystal.mc.player.canEntityBeSeen(entity) && AutoCrystal.mc.player.getDistanceSq(entity) <= MathUtil.square(this.breaktrace.getValue().floatValue()));
    }

    private boolean isEligableForFeetSync(EntityPlayer player, BlockPos pos) {
        if (this.holySync.getValue().booleanValue()) {
            BlockPos playerPos = new BlockPos(player.getPositionVector());
            for (EnumFacing facing : EnumFacing.values()) {
                if (facing == EnumFacing.DOWN || facing == EnumFacing.UP || !pos.equals((Object)playerPos.down().offset(facing))) continue;
                return true;
            }
            return false;
        }
        return true;
    }

    static {
        lowDmgPos = new ConcurrentSet();
        placedPos = new HashSet<BlockPos>();
        brokenPos = new HashSet<BlockPos>();
    }

    public static class PlaceInfo {
        private final BlockPos pos;
        private final boolean offhand;
        private final boolean placeSwing;
        private final boolean exactHand;
        private final boolean silent;
        public PlaceInfo(BlockPos pos, boolean offhand, boolean placeSwing, boolean exactHand, boolean silent) {
            this.pos = pos;
            this.offhand = offhand;
            this.placeSwing = placeSwing;
            this.exactHand = exactHand;
            this.silent = silent;
        }
        public void runPlace() {
            BlockUtil.placeCrystalOnBlock(this.pos, this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, this.placeSwing, this.exactHand, this.silent);
        }
    }

    private static class RAutoCrystal
    implements Runnable {
        private static RAutoCrystal instance;
        private AutoCrystal autoCrystal;

        private RAutoCrystal() {
        }

        public static RAutoCrystal getInstance(AutoCrystal autoCrystal) {
            if (instance == null) {
                instance = new RAutoCrystal();
                RAutoCrystal.instance.autoCrystal = autoCrystal;
            }
            return instance;
        }

        @Override
        public void run() {
            if (this.autoCrystal.threadMode.getValue() == ThreadMode.WHILE) {
                while (this.autoCrystal.isOn() && this.autoCrystal.threadMode.getValue() == ThreadMode.WHILE) {
                    while (Experium.eventManager.ticksOngoing()) {
                    }
                    if (this.autoCrystal.shouldInterrupt.get()) {
                        this.autoCrystal.shouldInterrupt.set(false);
                        this.autoCrystal.syncroTimer.reset();
                        this.autoCrystal.thread.interrupt();
                        break;
                    }
                    this.autoCrystal.threadOngoing.set(true);
                    Experium.safetyManager.doSafetyCheck();
                    this.autoCrystal.doAutoCrystal();
                    this.autoCrystal.threadOngoing.set(false);
                    try {
                        Thread.sleep(this.autoCrystal.threadDelay.getValue().intValue());
                    }
                    catch (InterruptedException e) {
                        this.autoCrystal.thread.interrupt();
                        e.printStackTrace();
                    }
                }
            } else if (this.autoCrystal.threadMode.getValue() != ThreadMode.NONE && this.autoCrystal.isOn()) {
                while (Experium.eventManager.ticksOngoing()) {
                }
                this.autoCrystal.threadOngoing.set(true);
                Experium.safetyManager.doSafetyCheck();
                this.autoCrystal.doAutoCrystal();
                this.autoCrystal.threadOngoing.set(false);
            }
        }
    }

    private class RenderPos {
        private BlockPos renderPos;
        private float renderTime;

        public RenderPos(BlockPos pos, float time) {
            this.renderPos = pos;
            this.renderTime = time;
        }

        public BlockPos getPos() {
            return this.renderPos;
        }

        public void setPos(BlockPos pos) {
            this.renderPos = pos;
        }

        public float getRenderTime() {
            return this.renderTime;
        }

        public void setRenderTime(float time) {
            this.renderTime = time;
        }
    }
	
	public static enum PredictTimer {
        NONE,
        BREAK,
        PREDICT;
    }

    public static enum AntiFriendPop {
        NONE,
        PLACE,
        BREAK,
        ALL;
    }

    public static enum ThreadMode {
        NONE,
        POOL,
        SOUND,
        WHILE;
    }

    public static enum AutoSwitch {
        NONE,
        TOGGLE,
        ALWAYS,
        SILENT;
    }

    public static enum Raytrace {
        NONE,
        PLACE,
        BREAK,
        FULL;
    }

    public static enum Switch {
        ALWAYS,
        BREAKSLOT,
        CALC;
    }

    public static enum Logic {
        BREAKPLACE,
        PLACEBREAK;
    }

    public static enum Target {
        CLOSEST,
        UNSAFE,
        DAMAGE;
    }

    public static enum Rotate {
        OFF,
        PLACE,
        BREAK,
        ALL;
    }

    public static enum DamageSync {
        NONE,
        PLACE,
        BREAK;
    }

    public static enum Settings {
        PLACE,
        BREAK,
        RENDER,
        MISC,
        DEV;
    }

    public static enum RenderMode {
        STATIC,
        FADE,
        GLIDE,
		GRADIENTRAINBOW;
    }
	
	public static enum Timing {
        Sequential,
        None;
    }
	
	public enum AntiWeaknessMode {
        Normal,
        Silent,
        None;
    }
}
