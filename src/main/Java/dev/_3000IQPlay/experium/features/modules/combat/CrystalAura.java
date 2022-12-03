package dev._3000IQPlay.experium.features.modules.combat;

import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.features.setting.Bind;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.event.events.PacketEvent;
import dev._3000IQPlay.experium.event.events.Render3DEvent;
import dev._3000IQPlay.experium.event.events.UpdateWalkingPlayerEvent;
import dev._3000IQPlay.experium.manager.RotationManager;
import dev._3000IQPlay.experium.mixin.mixins.accessors.ICPacketUseEntity;
import dev._3000IQPlay.experium.mixin.mixins.accessors.IEntityPlayerSP;
import dev._3000IQPlay.experium.mixin.mixins.accessors.IRenderManager;
import dev._3000IQPlay.experium.util.ColorUtil;
import dev._3000IQPlay.experium.util.CrystalUtil;
import dev._3000IQPlay.experium.util.EntityUtil;
import dev._3000IQPlay.experium.util.RenderUtil;
import dev._3000IQPlay.experium.util.Timer;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class CrystalAura
        extends Module {
	private static CrystalAura INSTANCE;
    private final Setting<Pages> setting = this.register(new Setting<Pages>("Page", Pages.General));
    public Setting<Boolean> noMineSwitch = this.register(new Setting<Boolean>("NoMining", Boolean.valueOf(false), v -> this.setting.getValue() == Pages.General));
    public Setting<Boolean> noGapSwitch = this.register(new Setting<Boolean>("NoGapping", Boolean.valueOf(false), v -> this.setting.getValue() == Pages.General));
    public Setting<Boolean> rightClickGap = this.register(new Setting<Boolean>("RightClickGap", Boolean.FALSE, v -> this.noGapSwitch.getValue() != false && this.setting.getValue() == Pages.General));
    public Setting<TimingMode> timingMode = this.register(new Setting<TimingMode>("Timing", TimingMode.NORMAL, timingMode -> this.setting.getValue() == Pages.General));
    public Setting<Boolean> inhibit = this.register(new Setting<Boolean>("Inhibit", Boolean.valueOf(false), v -> this.setting.getValue() == Pages.General));
    public Setting<Boolean> limit = this.register(new Setting<Boolean>("Limit", Boolean.valueOf(true), v -> this.setting.getValue() == Pages.General));
    public Setting<RotationMode> rotationMode = this.register(new Setting<RotationMode>("Rotate", RotationMode.TRACK, rotationMode -> this.setting.getValue() == Pages.General));
    public Setting<YawStepMode> yawStep = this.register(new Setting<YawStepMode>("YawStep", YawStepMode.OFF, yawStepMode -> this.setting.getValue() == Pages.General));
    public Setting<Float> yawAngle = this.register(new Setting<Float>("YawAngle", Float.valueOf(0.3f), Float.valueOf(0.1f), Float.valueOf(1.0f), f -> this.setting.getValue() == Pages.General));
    public Setting<Integer> yawTicks = this.register(new Setting<Integer>("YawTicks", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(5), n -> this.setting.getValue() == Pages.General));
    public Setting<Boolean> strictDirection = this.register(new Setting<Boolean>("StrictDirection", Boolean.valueOf(true), v -> this.setting.getValue() == Pages.General));
    public Setting<Boolean> swing = this.register(new Setting<Boolean>("Swing", Boolean.valueOf(true), v -> this.setting.getValue() == Pages.General));
    public Setting<SyncMode> syncMode = this.register(new Setting<SyncMode>("Sync", SyncMode.MERGE, syncMode -> this.setting.getValue() == Pages.General));
    public Setting<Float> mergeOffset = this.register(new Setting<Float>("MergeOffset", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(8.0f), f -> this.syncMode.getValue() == SyncMode.MERGE && this.setting.getValue() == Pages.General));
    public Setting<Float> enemyRange = this.register(new Setting<Float>("EnemyRange", Float.valueOf(8.0f), Float.valueOf(4.0f), Float.valueOf(15.0f), f -> this.setting.getValue() == Pages.General));
    public Setting<Float> crystalRange = this.register(new Setting<Float>("CrystalRange", Float.valueOf(6.0f), Float.valueOf(2.0f), Float.valueOf(12.0f), f -> this.setting.getValue() == Pages.General));
    public Setting<Float> disableUnderHealth = this.register(new Setting<Float>("DisableHealth", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(10.0f), f -> this.setting.getValue() == Pages.General));
    public Setting<Float> placeRange = this.register(new Setting<Float>("PlaceRange", Float.valueOf(4.0f), Float.valueOf(1.0f), Float.valueOf(6.0f), f -> this.setting.getValue() == Pages.Place));
    public Setting<Float> placeWallsRange = this.register(new Setting<Float>("PlaceWallsRange", Float.valueOf(3.0f), Float.valueOf(1.0f), Float.valueOf(6.0f), f -> this.setting.getValue() == Pages.Place));
    public Setting<Float> placeSpeed = this.register(new Setting<Float>("PlaceSpeed", Float.valueOf(20.0f), Float.valueOf(2.0f), Float.valueOf(20.0f), f -> this.setting.getValue() == Pages.Place));
    public Setting<ACSwapMode> autoSwap = this.register(new Setting<ACSwapMode>("AutoSwap", ACSwapMode.OFF, aCSwapMode -> this.setting.getValue() == Pages.Place));
    public Setting<Float> swapDelay = this.register(new Setting<Float>("SwapDelay", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(10.0f), f -> this.setting.getValue() == Pages.Place));
    public Setting<Boolean> check = this.register(new Setting<Boolean>("PlacementsCheck", Boolean.valueOf(true), v -> this.setting.getValue() == Pages.Place));
    public Setting<DirectionMode> directionMode = this.register(new Setting<DirectionMode>("Interact", DirectionMode.NORMAL, directionMode -> this.setting.getValue() == Pages.Place));
    public Setting<Boolean> protocol = this.register(new Setting<Boolean>("1.13+ Place", Boolean.valueOf(false), v -> this.setting.getValue() == Pages.Place));
    public Setting<Boolean> liquids = this.register(new Setting<Boolean>("PlaceInLiquids", Boolean.valueOf(false), v -> this.setting.getValue() == Pages.Place));
    public Setting<Boolean> fire = this.register(new Setting<Boolean>("PlaceInFire", Boolean.valueOf(false), v -> this.setting.getValue() == Pages.Place));
    public Setting<Float> breakRange = this.register(new Setting<Float>("BreakRange", Float.valueOf(4.3f), Float.valueOf(1.0f), Float.valueOf(6.0f), f -> this.setting.getValue() == Pages.Break));
    public Setting<Float> breakWallsRange = this.register(new Setting<Float>("BreakWalls", Float.valueOf(3.0f), Float.valueOf(1.0f), Float.valueOf(6.0f), f -> this.setting.getValue() == Pages.Break));
    public Setting<Integer> attackFactor = this.register(new Setting<Integer>("AttackFactor", Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(20), n -> this.setting.getValue() == Pages.Break));
    public Setting<ACAntiWeakness> antiWeakness = this.register(new Setting<ACAntiWeakness>("AntiWeakness", ACAntiWeakness.OFF, aCAntiWeakness -> this.setting.getValue() == Pages.Break));
    public Setting<Float> breakSpeed = this.register(new Setting<Float>("BreakSpeed", Float.valueOf(20.0f), Float.valueOf(1.0f), Float.valueOf(20.0f), f -> this.setting.getValue() == Pages.Break));
    public Setting<Boolean> collision = this.register(new Setting<Boolean>("Collision", Boolean.valueOf(false), v -> this.setting.getValue() == Pages.Break));
    public Setting<Integer> predictTicks = this.register(new Setting<Integer>("PredictTicks", Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(10), n -> this.setting.getValue() == Pages.Break));
    public Setting<Boolean> terrainIgnore = this.register(new Setting<Boolean>("TerrainTrace", Boolean.FALSE, v -> this.setting.getValue() == Pages.Break));
    public Setting<Boolean> predictPops = this.register(new Setting<Boolean>("PredictPops", Boolean.FALSE, v -> this.setting.getValue() == Pages.Break));
    public Setting<ConfirmMode> confirm = this.register(new Setting<ConfirmMode>("Confirm", ConfirmMode.OFF, confirmMode -> this.setting.getValue() == Pages.Calculation));
    public Setting<Integer> delay = this.register(new Setting<Integer>("TicksExisted", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(20), n -> this.setting.getValue() == Pages.Calculation));
    public Setting<TargetingMode> targetingMode = this.register(new Setting<TargetingMode>("Target", TargetingMode.ALL, targetingMode -> this.setting.getValue() == Pages.Calculation));
    public Setting<Float> security = this.register(new Setting<Float>("DamageBalance", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f), f -> this.setting.getValue() == Pages.Calculation));
    public Setting<Float> compromise = this.register(new Setting<Float>("Compromise", Float.valueOf(1.0f), Float.valueOf(0.05f), Float.valueOf(2.0f), f -> this.setting.getValue() == Pages.Calculation));
    public Setting<Float> minPlaceDamage = this.register(new Setting<Float>("MinDamage", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(20.0f), f -> this.setting.getValue() == Pages.Calculation));
    public Setting<Float> maxSelfPlace = this.register(new Setting<Float>("MaxSelfDmg", Float.valueOf(12.0f), Float.valueOf(0.0f), Float.valueOf(20.0f), f -> this.setting.getValue() == Pages.Calculation));
    public Setting<Float> suicideHealth = this.register(new Setting<Float>("SuicideHealth", Float.valueOf(2.0f), Float.valueOf(0.0f), Float.valueOf(10.0f), f -> this.setting.getValue() == Pages.Calculation));
    public Setting<Float> faceplaceHealth = this.register(new Setting<Float>("FaceplaceHealth", Float.valueOf(4.0f), Float.valueOf(0.0f), Float.valueOf(20.0f), f -> this.setting.getValue() == Pages.Calculation));
    public final Setting<Bind> forceFaceplace = this.register(new Setting<Bind>("Faceplace", new Bind(-1), bind -> this.setting.getValue() == Pages.Calculation));
    private final Setting<Boolean> armorBreaker = this.register(new Setting<Boolean>("ArmorBreaker", Boolean.valueOf(true), v -> this.setting.getValue() == Pages.Calculation));
    private final Setting<Float> depletion = this.register(new Setting<Float>("ArmorDepletion", Float.valueOf(0.9f), Float.valueOf(0.1f), Float.valueOf(1.0f), f -> this.armorBreaker.getValue() != false && this.setting.getValue() == Pages.Calculation));
    public Setting<RenderMode> renderMode = this.register(new Setting<RenderMode>("RenderMode", RenderMode.STATIC, renderMode -> this.setting.getValue() == Pages.Render));
    public Setting<Integer> RTimerr = this.register(new Setting<Integer>("RenderTimer", Integer.valueOf(300), Integer.valueOf(0), Integer.valueOf(1000), n -> this.setting.getValue() == Pages.Render));
    public Setting<Float> rainbowBrightness = this.register(new Setting<Object>("Brightness ", Float.valueOf(150.0f), Float.valueOf(1.0f), Float.valueOf(255.0f), object -> this.renderMode.getValue() == RenderMode.NEWRAINBOW && this.setting.getValue() == Pages.Render));
    public Setting<Float> rainbowSaturation = this.register(new Setting<Object>("Saturation", Float.valueOf(150.0f), Float.valueOf(1.0f), Float.valueOf(255.0f), object -> this.renderMode.getValue() == RenderMode.NEWRAINBOW && this.setting.getValue() == Pages.Render));
    private final Setting<Boolean> fadeFactor = this.register(new Setting<Boolean>("Fade", Boolean.valueOf(true), v -> this.renderMode.getValue() == RenderMode.FADE && this.setting.getValue() == Pages.Render));
    private final Setting<Boolean> scaleFactor = this.register(new Setting<Boolean>("Shrink", Boolean.valueOf(false), v -> this.renderMode.getValue() == RenderMode.FADE && this.setting.getValue() == Pages.Render));
    private final Setting<Boolean> slabFactor = this.register(new Setting<Boolean>("Slab", Boolean.valueOf(false), v -> this.renderMode.getValue() == RenderMode.FADE && this.setting.getValue() == Pages.Render));
    private final Setting<Float> duration = this.register(new Setting<Float>("Duration", Float.valueOf(1500.0f), Float.valueOf(0.0f), Float.valueOf(5000.0f), f -> this.renderMode.getValue() == RenderMode.FADE && this.setting.getValue() == Pages.Render));
    private final Setting<Integer> max = this.register(new Setting<Integer>("MaxPositions", Integer.valueOf(15), Integer.valueOf(1), Integer.valueOf(30), n -> this.renderMode.getValue() == RenderMode.FADE && this.setting.getValue() == Pages.Render));
    private final Setting<Float> slabHeight = this.register(new Setting<Float>("SlabDepth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(1.0f), f -> (this.renderMode.getValue() == RenderMode.STATIC || this.renderMode.getValue() == RenderMode.GLIDE) && this.setting.getValue() == Pages.Render));
    private final Setting<Float> moveSpeed = this.register(new Setting<Float>("Speed", Float.valueOf(900.0f), Float.valueOf(0.0f), Float.valueOf(1500.0f), f -> this.renderMode.getValue() == RenderMode.GLIDE && this.setting.getValue() == Pages.Render));
    private final Setting<Float> accel = this.register(new Setting<Float>("Deceleration", Float.valueOf(0.8f), Float.valueOf(0.0f), Float.valueOf(1.0f), f -> this.renderMode.getValue() == RenderMode.GLIDE && this.setting.getValue() == Pages.Render));
    public Setting<Boolean> colorSync = this.register(new Setting<Object>("ColorSync", Boolean.valueOf(true), object -> this.setting.getValue() == Pages.Render));
    public Setting<Boolean> box = this.register(new Setting<Object>("Box", Boolean.valueOf(true), object -> this.setting.getValue() == Pages.Render));
	private final Setting<Color> bC = register(new Setting("BoxColor", new Color(40, 192, 255, 40), object -> this.box.getValue() != false && this.setting.getValue() == Pages.Render));
    public Setting<Boolean> outline = this.register(new Setting<Object>("Outline", Boolean.valueOf(true), object -> this.setting.getValue() == Pages.Render));
	private final Setting<Color> oC = register(new Setting("OutlineColor", new Color(40, 192, 255, 255), object -> this.outline.getValue() != false && this.setting.getValue() == Pages.Render));
    private final Setting<Float> lineWidth = this.register(new Setting<Object>("LineWidth", Float.valueOf(1.8f), Float.valueOf(0.1f), Float.valueOf(5.0f), object -> this.outline.getValue() != false && this.setting.getValue() == Pages.Render));
    public Setting<Boolean> text = this.register(new Setting<Object>("RenderDmg", Boolean.valueOf(true), object -> this.setting.getValue() == Pages.Render));
    public Vec3d rotationVector = null;
    public float[] rotations = new float[]{0.0f, 0.0f};
    public Timer rotationTimer = new Timer();
    private BlockPos prevPlacePos = null;
    public final Timer placeTimer = new Timer();
    public final Timer breakTimer = new Timer();
    public final Timer swapTimer = new Timer();
    public float renderDamage = 0.0f;
    public final Timer renderTimeoutTimer = new Timer();
    public final Timer renderBreakingTimer = new Timer();
    public boolean isPlacing = false;
    public final ConcurrentHashMap<BlockPos, Long> placeLocations = new ConcurrentHashMap();
    public final ConcurrentHashMap<Integer, Long> breakLocations = new ConcurrentHashMap();
    public final Map<EntityPlayer, Timer> totemPops = new ConcurrentHashMap<EntityPlayer, Timer>();
    public final List<BlockPos> selfPlacePositions = new CopyOnWriteArrayList<BlockPos>();
    public AtomicBoolean tickRunning = new AtomicBoolean(false);
    public final Timer linearTimer = new Timer();
    public final Timer cacheTimer = new Timer();
    public BlockPos cachePos = null;
    public final Timer inhibitTimer = new Timer();
    public EntityEnderCrystal inhibitEntity = null;
    private final Timer scatterTimer = new Timer();
    private Vec3d bilateralVec = null;
    private AtomicBoolean shouldRunThread = new AtomicBoolean(false);
    private AtomicBoolean lastBroken = new AtomicBoolean(false);
    private Timer renderTargetTimer = new Timer();
    private boolean foundDoublePop = false;
    private float displayself = 0.0f;
    public int oldSlotCrystal = -1;
    public int oldSlotSword = -1;
    private final List<RenderPos> positions = new ArrayList<RenderPos>();
    private AxisAlignedBB renderBB;
    private BlockPos lastRenderPos;
	private static float test1;
    public BlockPos renderBreakingPos;
    private Thread thread;
    private BlockPos postPlacePos;
    private EntityEnderCrystal postBreakPos;
    private EntityPlayer renderTarget;
    private float timePassed;
    private RayTraceResult postResult;
    private int ticks;
    private static float aboba;
    private EnumFacing postFacing;
    public BlockPos renderBlock;
	
	public CrystalAura() {
        super("CrystalAura", "Best AutoCrystal", Module.Category.COMBAT, true, false, false);
        this.setInstance();
    }
	
	public static CrystalAura getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CrystalAura();
        }
        return INSTANCE;
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayerPre(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0) {
            this.placeLocations.forEach((blockPos, l) -> {
                if (System.currentTimeMillis() - l > 1500L) {
                    this.placeLocations.remove(blockPos);
                }
            });
            --this.ticks;
            if (this.bilateralVec != null) {
                for (Entity entity : CrystalAura.mc.world.loadedEntityList) {
                    if (!(entity instanceof EntityEnderCrystal) || !(entity.getDistance(this.bilateralVec.x, this.bilateralVec.y, this.bilateralVec.z) <= 6.0)) continue;
                    this.breakLocations.put(entity.getEntityId(), System.currentTimeMillis());
                }
                this.bilateralVec = null;
            }
            if (event.isCanceled()) {
                return;
            }
            this.postBreakPos = null;
            this.postPlacePos = null;
            this.postFacing = null;
            this.postResult = null;
            this.foundDoublePop = false;
            this.handleSequential();
            if (this.rotationMode.getValue() != RotationMode.OFF && !this.rotationTimer.passedMs(650L) && this.rotationVector != null) {
                if (this.rotationMode.getValue() == RotationMode.TRACK) {
                    this.rotations = RotationManager.calculateAngle(CrystalAura.mc.player.getPositionEyes(1.0f), this.rotationVector);
                }
                if (this.yawAngle.getValue().floatValue() < 1.0f && this.yawStep.getValue() != YawStepMode.OFF && (this.postBreakPos != null || this.yawStep.getValue() == YawStepMode.FULL)) {
                    if (this.ticks > 0) {
                        this.rotations[0] = ((IEntityPlayerSP)CrystalAura.mc.player).getLastReportedYaw();
                        this.postBreakPos = null;
                        this.postPlacePos = null;
                    } else {
                        float f = MathHelper.wrapDegrees((float)(this.rotations[0] - ((IEntityPlayerSP)CrystalAura.mc.player).getLastReportedYaw()));
                        if (Math.abs(f) > 180.0f * this.yawAngle.getValue().floatValue()) {
                            this.rotations[0] = ((IEntityPlayerSP)CrystalAura.mc.player).getLastReportedYaw() + f * (180.0f * this.yawAngle.getValue().floatValue() / Math.abs(f));
                            this.postBreakPos = null;
                            this.postPlacePos = null;
                            this.ticks = this.yawTicks.getValue();
                        }
                    }
                }
                CrystalAura.lookAtAngles(this.rotations[0], this.rotations[1]);
            }
        }
    }

    public BlockPos getPostPlacePos() {
        return this.postPlacePos;
    }

    private boolean breakCrystal(EntityEnderCrystal entityEnderCrystal) {
        if (entityEnderCrystal != null) {
            if (this.antiWeakness.getValue() != ACAntiWeakness.OFF && CrystalAura.mc.player.isPotionActive(MobEffects.WEAKNESS) && !(CrystalAura.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword) && !this.switchToSword()) {
                return false;
            }
            if (!this.swapTimer.passedMs((long)(this.swapDelay.getValue().floatValue() * 100.0f))) {
                return false;
            }
            CrystalAura.mc.playerController.attackEntity((EntityPlayer)CrystalAura.mc.player, (Entity)entityEnderCrystal);
            CrystalAura.mc.player.connection.sendPacket((Packet)new CPacketAnimation(this.isOffhand() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));
            this.swingArmAfterBreaking(this.isOffhand() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
            if (this.oldSlotSword != -1 && CrystalAura.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword) {
                CrystalAura.mc.player.inventory.currentItem = this.oldSlotSword;
                CrystalAura.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.oldSlotSword));
                this.oldSlotSword = -1;
            }
            if (this.syncMode.getValue() == SyncMode.MERGE) {
                this.placeTimer.reset();
            }
            if (this.syncMode.getValue() == SyncMode.STRICT) {
                this.lastBroken.set(true);
            }
            this.inhibitTimer.reset();
            this.inhibitEntity = entityEnderCrystal;
            this.renderBreakingPos = new BlockPos((Entity)entityEnderCrystal).down();
            this.renderBreakingTimer.reset();
            return true;
        }
        return false;
    }

    private BlockPos findPlacePosition(List<BlockPos> list, List<EntityPlayer> list2) {
        if (list2.isEmpty()) {
            return null;
        }
        float f = 0.5f;
        EntityPlayer entityPlayer = null;
        BlockPos blockPos = null;
        this.foundDoublePop = false;
        EntityPlayer entityPlayer2 = null;
        for (BlockPos blockPos2 : list) {
            float f2 = CrystalUtil.calculateDamage(blockPos2, (Entity)CrystalAura.mc.player);
            if (!((double)f2 + (double)this.suicideHealth.getValue().floatValue() < (double)(CrystalAura.mc.player.getHealth() + CrystalAura.mc.player.getAbsorptionAmount())) || !(f2 <= this.maxSelfPlace.getValue().floatValue())) continue;
            if (this.targetingMode.getValue() != TargetingMode.ALL) {
                entityPlayer2 = list2.get(0);
                if (entityPlayer2.getDistance((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.5, (double)blockPos2.getZ() + 0.5) > (double)this.crystalRange.getValue().floatValue()) continue;
                float f3 = CrystalUtil.calculateDamage(blockPos2, (Entity)entityPlayer2);
                if (this.isDoublePoppable(entityPlayer2, f3) && (blockPos == null || entityPlayer2.getDistanceSq(blockPos2) < entityPlayer2.getDistanceSq(blockPos))) {
                    entityPlayer = entityPlayer2;
                    f = f3;
                    blockPos = blockPos2;
                    this.foundDoublePop = true;
                    continue;
                }
                if (this.foundDoublePop || !(f3 > f) || !(f3 * this.compromise.getValue().floatValue() > f2) && !(f3 > entityPlayer2.getHealth() + entityPlayer2.getAbsorptionAmount()) || f3 < this.minPlaceDamage.getValue().floatValue() && entityPlayer2.getHealth() + entityPlayer2.getAbsorptionAmount() > this.faceplaceHealth.getValue().floatValue() && !this.forceFaceplace.getValue().isDown() && !this.shouldArmorBreak(entityPlayer2)) continue;
                f = f3;
                entityPlayer = entityPlayer2;
                blockPos = blockPos2;
                continue;
            }
            for (EntityPlayer entityPlayer3 : list2) {
                if (entityPlayer3.equals((Object)entityPlayer2) || entityPlayer3.getDistance((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.5, (double)blockPos2.getZ() + 0.5) > (double)this.crystalRange.getValue().floatValue()) continue;
                float f4 = CrystalUtil.calculateDamage(blockPos2, (Entity)entityPlayer3);
                if (this.isDoublePoppable(entityPlayer3, f4) && (blockPos == null || entityPlayer3.getDistanceSq(blockPos2) < entityPlayer3.getDistanceSq(blockPos))) {
                    entityPlayer = entityPlayer3;
                    f = f4;
                    blockPos = blockPos2;
                    this.foundDoublePop = true;
                    continue;
                }
                if (this.foundDoublePop || !(f4 > f) || !(f4 * this.compromise.getValue().floatValue() > f2) && !(f4 > entityPlayer3.getHealth() + entityPlayer3.getAbsorptionAmount()) || f4 < this.minPlaceDamage.getValue().floatValue() && entityPlayer3.getHealth() + entityPlayer3.getAbsorptionAmount() > this.faceplaceHealth.getValue().floatValue() && !this.forceFaceplace.getValue().isDown() && !this.shouldArmorBreak(entityPlayer3)) continue;
                f = f4;
                entityPlayer = entityPlayer3;
                blockPos = blockPos2;
            }
        }
        if (entityPlayer != null && blockPos != null) {
            this.renderTarget = entityPlayer;
            this.renderTargetTimer.reset();
        }
        if (blockPos != null) {
            this.renderBlock = blockPos;
            this.renderDamage = f;
        }
        this.cachePos = blockPos;
        this.cacheTimer.reset();
        return blockPos;
    }

    @Override
    public String getDisplayInfo() {
        test1 = this.renderTargetTimer.getPassedTimeMs() / 10L;
        if (this.renderTarget != null && !this.renderTargetTimer.passedMs(800L)) {
            return String.valueOf(new StringBuilder().append(this.renderTarget.getName()).append(" , ").append((int)test1).append("ms , ").append(Math.floor(this.renderDamage) == (double)this.renderDamage ? Integer.valueOf((int)this.renderDamage) : String.format("%.1f", Float.valueOf(this.renderDamage))).append(""));
        }
        return null;
    }

    public EnumFacing handlePlaceRotation(BlockPos blockPos) {
        if (blockPos == null || CrystalAura.mc.player == null) {
            return null;
        }
        EnumFacing enumFacing = null;
        if (this.directionMode.getValue() != DirectionMode.VANILLA) {
            double[] arrd;
            Vec3d vec3d;
            RayTraceResult rayTraceResult;
            Vec3d vec3d2;
            Vec3d vec3d3;
            float f;
            float f2;
            float f3;
            float f4;
            double[] arrd2;
            double d;
            double d2;
            double d3;
            double d4;
            double d5;
            Vec3d vec3d4;
            double d6;
            double d7;
            double d8;
            Vec3d vec3d5 = null;
            double[] arrd3 = null;
            double d9 = 0.45;
            double d10 = 0.05;
            double d11 = 0.95;
            Vec3d vec3d6 = new Vec3d(CrystalAura.mc.player.posX, CrystalAura.mc.player.getEntityBoundingBox().minY + (double)CrystalAura.mc.player.getEyeHeight(), CrystalAura.mc.player.posZ);
            for (d8 = d10; d8 <= d11; d8 += d9) {
                for (d7 = d10; d7 <= d11; d7 += d9) {
                    for (d6 = d10; d6 <= d11; d6 += d9) {
                        vec3d4 = new Vec3d((Vec3i)blockPos).add(d8, d7, d6);
                        d5 = vec3d6.distanceTo(vec3d4);
                        d4 = vec3d4.x - vec3d6.x;
                        d3 = vec3d4.y - vec3d6.y;
                        d2 = vec3d4.z - vec3d6.z;
                        d = MathHelper.sqrt((double)(d4 * d4 + d2 * d2));
                        arrd2 = new double[]{MathHelper.wrapDegrees((float)((float)Math.toDegrees(Math.atan2(d2, d4)) - 90.0f)), MathHelper.wrapDegrees((float)((float)(-Math.toDegrees(Math.atan2(d3, d)))))};
                        f4 = MathHelper.cos((float)((float)(-arrd2[0] * 0.01745329238474369 - 3.1415927410125732)));
                        f3 = MathHelper.sin((float)((float)(-arrd2[0] * 0.01745329238474369 - 3.1415927410125732)));
                        f2 = -MathHelper.cos((float)((float)(-arrd2[1] * 0.01745329238474369)));
                        f = MathHelper.sin((float)((float)(-arrd2[1] * 0.01745329238474369)));
                        vec3d3 = new Vec3d((double)(f3 * f2), (double)f, (double)(f4 * f2));
                        vec3d2 = vec3d6.add(vec3d3.x * d5, vec3d3.y * d5, vec3d3.z * d5);
                        rayTraceResult = CrystalAura.mc.world.rayTraceBlocks(vec3d6, vec3d2, false, true, false);
                        if (!(this.placeWallsRange.getValue().floatValue() >= this.placeRange.getValue().floatValue() || rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK && rayTraceResult.getBlockPos().equals((Object)blockPos))) continue;
                        vec3d = vec3d4;
                        arrd = arrd2;
                        if (this.strictDirection.getValue().booleanValue()) {
                            if (vec3d5 != null && arrd3 != null && (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK || enumFacing == null)) {
                                if (!(CrystalAura.mc.player.getPositionVector().add(0.0, (double)CrystalAura.mc.player.getEyeHeight(), 0.0).distanceTo(vec3d) < CrystalAura.mc.player.getPositionVector().add(0.0, (double)CrystalAura.mc.player.getEyeHeight(), 0.0).distanceTo(vec3d5))) continue;
                                vec3d5 = vec3d;
                                arrd3 = arrd;
                                if (rayTraceResult == null || rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK) continue;
                                enumFacing = rayTraceResult.sideHit;
                                this.postResult = rayTraceResult;
                                continue;
                            }
                            vec3d5 = vec3d;
                            arrd3 = arrd;
                            if (rayTraceResult == null || rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK) continue;
                            enumFacing = rayTraceResult.sideHit;
                            this.postResult = rayTraceResult;
                            continue;
                        }
                        if (vec3d5 != null && arrd3 != null && (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK || enumFacing == null)) {
                            if (!(Math.hypot(((arrd[0] - (double)((IEntityPlayerSP)CrystalAura.mc.player).getLastReportedYaw()) % 360.0 + 540.0) % 360.0 - 180.0, arrd[1] - (double)((IEntityPlayerSP)CrystalAura.mc.player).getLastReportedPitch()) < Math.hypot(((arrd3[0] - (double)((IEntityPlayerSP)CrystalAura.mc.player).getLastReportedYaw()) % 360.0 + 540.0) % 360.0 - 180.0, arrd3[1] - (double)((IEntityPlayerSP)CrystalAura.mc.player).getLastReportedPitch()))) continue;
                            vec3d5 = vec3d;
                            arrd3 = arrd;
                            if (rayTraceResult == null || rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK) continue;
                            enumFacing = rayTraceResult.sideHit;
                            this.postResult = rayTraceResult;
                            continue;
                        }
                        vec3d5 = vec3d;
                        arrd3 = arrd;
                        if (rayTraceResult == null || rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK) continue;
                        enumFacing = rayTraceResult.sideHit;
                        this.postResult = rayTraceResult;
                    }
                }
            }
            if (this.placeWallsRange.getValue().floatValue() < this.placeRange.getValue().floatValue() && this.directionMode.getValue() == DirectionMode.STRICT) {
                if (arrd3 != null && enumFacing != null) {
                    this.rotationTimer.reset();
                    this.rotationVector = vec3d5;
                    this.rotations = RotationManager.calculateAngle(CrystalAura.mc.player.getPositionEyes(1.0f), this.rotationVector);
                    return enumFacing;
                }
                for (d8 = d10; d8 <= d11; d8 += d9) {
                    for (d7 = d10; d7 <= d11; d7 += d9) {
                        for (d6 = d10; d6 <= d11; d6 += d9) {
                            vec3d4 = new Vec3d((Vec3i)blockPos).add(d8, d7, d6);
                            d5 = vec3d6.distanceTo(vec3d4);
                            d4 = vec3d4.x - vec3d6.x;
                            d3 = vec3d4.y - vec3d6.y;
                            d2 = vec3d4.z - vec3d6.z;
                            d = MathHelper.sqrt((double)(d4 * d4 + d2 * d2));
                            arrd2 = new double[]{MathHelper.wrapDegrees((float)((float)Math.toDegrees(Math.atan2(d2, d4)) - 90.0f)), MathHelper.wrapDegrees((float)((float)(-Math.toDegrees(Math.atan2(d3, d)))))};
                            f4 = MathHelper.cos((float)((float)(-arrd2[0] * 0.01745329238474369 - 3.1415927410125732)));
                            f3 = MathHelper.sin((float)((float)(-arrd2[0] * 0.01745329238474369 - 3.1415927410125732)));
                            f2 = -MathHelper.cos((float)((float)(-arrd2[1] * 0.01745329238474369)));
                            f = MathHelper.sin((float)((float)(-arrd2[1] * 0.01745329238474369)));
                            vec3d3 = new Vec3d((double)(f3 * f2), (double)f, (double)(f4 * f2));
                            vec3d2 = vec3d6.add(vec3d3.x * d5, vec3d3.y * d5, vec3d3.z * d5);
                            rayTraceResult = CrystalAura.mc.world.rayTraceBlocks(vec3d6, vec3d2, false, true, true);
                            if (rayTraceResult == null || rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK) continue;
                            vec3d = vec3d4;
                            arrd = arrd2;
                            if (this.strictDirection.getValue().booleanValue()) {
                                if (vec3d5 != null && arrd3 != null && (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK || enumFacing == null)) {
                                    if (!(CrystalAura.mc.player.getPositionVector().add(0.0, (double)CrystalAura.mc.player.getEyeHeight(), 0.0).distanceTo(vec3d) < CrystalAura.mc.player.getPositionVector().add(0.0, (double)CrystalAura.mc.player.getEyeHeight(), 0.0).distanceTo(vec3d5))) continue;
                                    vec3d5 = vec3d;
                                    arrd3 = arrd;
                                    if (rayTraceResult == null || rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK) continue;
                                    enumFacing = rayTraceResult.sideHit;
                                    this.postResult = rayTraceResult;
                                    continue;
                                }
                                vec3d5 = vec3d;
                                arrd3 = arrd;
                                if (rayTraceResult == null || rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK) continue;
                                enumFacing = rayTraceResult.sideHit;
                                this.postResult = rayTraceResult;
                                continue;
                            }
                            if (vec3d5 != null && arrd3 != null && (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK || enumFacing == null)) {
                                if (!(Math.hypot(((arrd[0] - (double)((IEntityPlayerSP)CrystalAura.mc.player).getLastReportedYaw()) % 360.0 + 540.0) % 360.0 - 180.0, arrd[1] - (double)((IEntityPlayerSP)CrystalAura.mc.player).getLastReportedPitch()) < Math.hypot(((arrd3[0] - (double)((IEntityPlayerSP)CrystalAura.mc.player).getLastReportedYaw()) % 360.0 + 540.0) % 360.0 - 180.0, arrd3[1] - (double)((IEntityPlayerSP)CrystalAura.mc.player).getLastReportedPitch()))) continue;
                                vec3d5 = vec3d;
                                arrd3 = arrd;
                                if (rayTraceResult == null || rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK) continue;
                                enumFacing = rayTraceResult.sideHit;
                                this.postResult = rayTraceResult;
                                continue;
                            }
                            vec3d5 = vec3d;
                            arrd3 = arrd;
                            if (rayTraceResult == null || rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK) continue;
                            enumFacing = rayTraceResult.sideHit;
                            this.postResult = rayTraceResult;
                        }
                    }
                }
            } else {
                if (arrd3 != null) {
                    this.rotationTimer.reset();
                    this.rotationVector = vec3d5;
                    this.rotations = RotationManager.calculateAngle(CrystalAura.mc.player.getPositionEyes(1.0f), this.rotationVector);
                }
                if (enumFacing != null) {
                    return enumFacing;
                }
            }
        } else {
            Vec3d vec3d;
            EnumFacing enumFacing2 = null;
            Vec3d vec3d7 = null;
            for (EnumFacing enumFacing3 : EnumFacing.values()) {
                vec3d = new Vec3d((double)blockPos.getX() + 0.5 + (double)enumFacing3.getDirectionVec().getX() * 0.5, (double)blockPos.getY() + 0.5 + (double)enumFacing3.getDirectionVec().getY() * 0.5, (double)blockPos.getZ() + 0.5 + (double)enumFacing3.getDirectionVec().getZ() * 0.5);
                RayTraceResult rayTraceResult = CrystalAura.mc.world.rayTraceBlocks(new Vec3d(CrystalAura.mc.player.posX, CrystalAura.mc.player.posY + (double)CrystalAura.mc.player.getEyeHeight(), CrystalAura.mc.player.posZ), vec3d, false, true, false);
                if (rayTraceResult == null || !rayTraceResult.typeOfHit.equals((Object)RayTraceResult.Type.BLOCK) || !rayTraceResult.getBlockPos().equals((Object)blockPos)) continue;
                if (this.strictDirection.getValue().booleanValue()) {
                    if (vec3d7 != null && !(CrystalAura.mc.player.getPositionVector().add(0.0, (double)CrystalAura.mc.player.getEyeHeight(), 0.0).distanceTo(vec3d) < CrystalAura.mc.player.getPositionVector().add(0.0, (double)CrystalAura.mc.player.getEyeHeight(), 0.0).distanceTo(vec3d7))) continue;
                    vec3d7 = vec3d;
                    enumFacing2 = enumFacing3;
                    this.postResult = rayTraceResult;
                    continue;
                }
                this.rotationTimer.reset();
                this.rotationVector = vec3d;
                this.rotations = RotationManager.calculateAngle(CrystalAura.mc.player.getPositionEyes(1.0f), this.rotationVector);
                return enumFacing3;
            }
            if (enumFacing2 != null) {
                this.rotationTimer.reset();
                this.rotationVector = vec3d7;
                this.rotations = RotationManager.calculateAngle(CrystalAura.mc.player.getPositionEyes(1.0f), this.rotationVector);
                return enumFacing2;
            }
            if (this.strictDirection.getValue().booleanValue()) {
                for (EnumFacing enumFacing3 : EnumFacing.values()) {
                    vec3d = new Vec3d((double)blockPos.getX() + 0.5 + (double)enumFacing3.getDirectionVec().getX() * 0.5, (double)blockPos.getY() + 0.5 + (double)enumFacing3.getDirectionVec().getY() * 0.5, (double)blockPos.getZ() + 0.5 + (double)enumFacing3.getDirectionVec().getZ() * 0.5);
                    if (vec3d7 != null && !(CrystalAura.mc.player.getPositionVector().add(0.0, (double)CrystalAura.mc.player.getEyeHeight(), 0.0).distanceTo(vec3d) < CrystalAura.mc.player.getPositionVector().add(0.0, (double)CrystalAura.mc.player.getEyeHeight(), 0.0).distanceTo(vec3d7))) continue;
                    vec3d7 = vec3d;
                    enumFacing2 = enumFacing3;
                }
                if (enumFacing2 != null) {
                    this.rotationTimer.reset();
                    this.rotationVector = vec3d7;
                    this.rotations = RotationManager.calculateAngle(CrystalAura.mc.player.getPositionEyes(1.0f), this.rotationVector);
                    return enumFacing2;
                }
            }
        }
        if ((double)blockPos.getY() > CrystalAura.mc.player.posY + (double)CrystalAura.mc.player.getEyeHeight()) {
            this.rotationTimer.reset();
            this.rotationVector = new Vec3d((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 1.0, (double)blockPos.getZ() + 0.5);
            this.rotations = RotationManager.calculateAngle(CrystalAura.mc.player.getPositionEyes(1.0f), this.rotationVector);
            return EnumFacing.DOWN;
        }
        this.rotationTimer.reset();
        this.rotationVector = new Vec3d((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 1.0, (double)blockPos.getZ() + 0.5);
        this.rotations = RotationManager.calculateAngle(CrystalAura.mc.player.getPositionEyes(1.0f), this.rotationVector);
        return EnumFacing.UP;
    }

    private void handleSequential() {
        if (CrystalAura.mc.player.getHealth() + CrystalAura.mc.player.getAbsorptionAmount() < this.disableUnderHealth.getValue().floatValue() || this.noGapSwitch.getValue() != false && CrystalAura.mc.player.getActiveItemStack().getItem() instanceof ItemFood || this.noMineSwitch.getValue().booleanValue() && CrystalAura.mc.playerController.getIsHittingBlock() && CrystalAura.mc.player.getHeldItemMainhand().getItem() instanceof ItemTool) {
            this.rotationVector = null;
            return;
        }
        if (this.noGapSwitch.getValue().booleanValue() && this.rightClickGap.getValue().booleanValue() && CrystalAura.mc.gameSettings.keyBindUseItem.isKeyDown() && CrystalAura.mc.player.inventory.getCurrentItem().getItem() instanceof ItemEndCrystal) {
            int n = -1;
            for (int i = 0; i < 9; ++i) {
                if (CrystalAura.mc.player.inventory.getStackInSlot(i).getItem() != Items.GOLDEN_APPLE) continue;
                n = i;
                break;
            }
            if (n != -1 && n != CrystalAura.mc.player.inventory.currentItem) {
                CrystalAura.mc.player.inventory.currentItem = n;
                CrystalAura.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(n));
                return;
            }
        }
        if (!this.isOffhand() && !(CrystalAura.mc.player.inventory.getCurrentItem().getItem() instanceof ItemEndCrystal) && this.autoSwap.getValue() == ACSwapMode.OFF) {
            return;
        }
        List<EntityPlayer> list = this.getTargetsInRange();
        EntityEnderCrystal entityEnderCrystal = this.findCrystalTarget(list);
        int n = (int)Math.max(100.0f, (float)(CrystalUtil.ping() + 50) / 1.0f) + 150;
        if (entityEnderCrystal != null && this.breakTimer.passedMs((long)(1000.0f - this.breakSpeed.getValue().floatValue() * 50.0f)) && (entityEnderCrystal.ticksExisted >= this.delay.getValue() || this.timingMode.getValue() == TimingMode.NORMAL)) {
            this.postBreakPos = entityEnderCrystal;
            this.handleBreakRotation(this.postBreakPos.posX, this.postBreakPos.posY, this.postBreakPos.posZ);
        }
        if (entityEnderCrystal == null && (this.confirm.getValue() != ConfirmMode.FULL || this.inhibitEntity == null || (double)this.inhibitEntity.ticksExisted >= Math.floor(this.delay.getValue().intValue())) && (this.syncMode.getValue() != SyncMode.STRICT || this.breakTimer.passedMs((long)(950.0f - this.breakSpeed.getValue().floatValue() * 50.0f - (float)CrystalUtil.ping()))) && this.placeTimer.passedMs((long)(1000.0f - this.placeSpeed.getValue().floatValue() * 50.0f)) && (this.timingMode.getValue() == TimingMode.SEQUENTIAL || this.linearTimer.passedMs((long)((float)this.delay.getValue().intValue() * 5.0f)))) {
            BlockPos blockPos;
            if (this.confirm.getValue() != ConfirmMode.OFF && this.cachePos != null && !this.cacheTimer.passedMs(n + 100) && this.canPlaceCrystal(this.cachePos)) {
                this.postPlacePos = this.cachePos;
                this.postFacing = this.handlePlaceRotation(this.postPlacePos);
                this.lastBroken.set(false);
                return;
            }
            List<BlockPos> list2 = this.findCrystalBlocks();
            if (!list2.isEmpty() && (blockPos = this.findPlacePosition(list2, list)) != null) {
                this.postPlacePos = blockPos;
                this.postFacing = this.handlePlaceRotation(this.postPlacePos);
            }
        }
        this.lastBroken.set(false);
    }

    @Override
    public void onEnable() {
        this.postBreakPos = null;
        this.postPlacePos = null;
        this.postFacing = null;
        this.postResult = null;
        this.prevPlacePos = null;
        this.cachePos = null;
        this.bilateralVec = null;
        this.lastBroken.set(false);
        this.rotationVector = null;
        this.rotationTimer.reset();
        this.isPlacing = false;
        this.foundDoublePop = false;
        this.totemPops.clear();
        this.oldSlotCrystal = -1;
        this.oldSlotSword = -1;
    }

    public boolean canPlaceCrystal(BlockPos blockPos) {
        if (CrystalAura.mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && CrystalAura.mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
            return false;
        }
        BlockPos blockPos2 = blockPos.add(0, 1, 0);
        if (!(CrystalAura.mc.world.getBlockState(blockPos2).getBlock() == Blocks.AIR || CrystalAura.mc.world.getBlockState(blockPos2).getBlock() == Blocks.FIRE && this.fire.getValue().booleanValue() || CrystalAura.mc.world.getBlockState(blockPos2).getBlock() instanceof BlockLiquid && this.liquids.getValue().booleanValue())) {
            return false;
        }
        BlockPos blockPos3 = blockPos.add(0, 2, 0);
        if (!(this.protocol.getValue().booleanValue() || CrystalAura.mc.world.getBlockState(blockPos3).getBlock() == Blocks.AIR || CrystalAura.mc.world.getBlockState(blockPos2).getBlock() instanceof BlockLiquid && this.liquids.getValue().booleanValue())) {
            return false;
        }
        if (this.check.getValue().booleanValue() && !CrystalUtil.rayTraceBreak((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 1.0, (double)blockPos.getZ() + 0.5)) {
            Vec3d vec3d = new Vec3d((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 1.0, (double)blockPos.getZ() + 0.5);
            if (CrystalAura.mc.player.getPositionEyes(1.0f).distanceTo(vec3d) > (double)this.breakWallsRange.getValue().floatValue()) {
                return false;
            }
        }
        if (this.placeWallsRange.getValue().floatValue() < this.placeRange.getValue().floatValue()) {
            if (!CrystalUtil.rayTracePlace(blockPos)) {
                if (this.strictDirection.getValue().booleanValue()) {
                    boolean bl;
                    block26: {
                        Vec3d vec3d = CrystalAura.mc.player.getPositionVector().add(0.0, (double)CrystalAura.mc.player.getEyeHeight(), 0.0);
                        bl = false;
                        if (this.directionMode.getValue() == DirectionMode.VANILLA) {
                            for (EnumFacing enumFacing : EnumFacing.values()) {
                                Vec3d vec3d2 = new Vec3d((double)blockPos.getX() + 0.5 + (double)enumFacing.getDirectionVec().getX() * 0.5, (double)blockPos.getY() + 0.5 + (double)enumFacing.getDirectionVec().getY() * 0.5, (double)blockPos.getZ() + 0.5 + (double)enumFacing.getDirectionVec().getZ() * 0.5);
                                if (!(vec3d.distanceTo(vec3d2) <= (double)this.placeWallsRange.getValue().floatValue())) continue;
                                bl = true;
                                break;
                            }
                        } else {
                            double d = 0.45;
                            double d2 = 0.05;
                            double d3 = 0.95;
                            for (double d4 = d2; d4 <= d3; d4 += d) {
                                for (double d5 = d2; d5 <= d3; d5 += d) {
                                    for (double d6 = d2; d6 <= d3; d6 += d) {
                                        Vec3d vec3d3 = new Vec3d((Vec3i)blockPos).add(d4, d5, d6);
                                        double d7 = vec3d.distanceTo(vec3d3);
                                        if (!(d7 <= (double)this.placeWallsRange.getValue().floatValue())) continue;
                                        bl = true;
                                        break block26;
                                    }
                                }
                            }
                        }
                    }
                    if (!bl) {
                        return false;
                    }
                } else if ((double)blockPos.getY() > CrystalAura.mc.player.posY + (double)CrystalAura.mc.player.getEyeHeight() ? CrystalAura.mc.player.getDistance((double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5) > (double)this.placeWallsRange.getValue().floatValue() : CrystalAura.mc.player.getDistance((double)blockPos.getX() + 0.5, (double)(blockPos.getY() + 1), (double)blockPos.getZ() + 0.5) > (double)this.placeWallsRange.getValue().floatValue()) {
                    return false;
                }
            }
        } else if (this.strictDirection.getValue().booleanValue()) {
            boolean bl;
            block27: {
                Vec3d vec3d = CrystalAura.mc.player.getPositionVector().add(0.0, (double)CrystalAura.mc.player.getEyeHeight(), 0.0);
                bl = false;
                if (this.directionMode.getValue() == DirectionMode.VANILLA) {
                    for (EnumFacing enumFacing : EnumFacing.values()) {
                        Vec3d vec3d4 = new Vec3d((double)blockPos.getX() + 0.5 + (double)enumFacing.getDirectionVec().getX() * 0.5, (double)blockPos.getY() + 0.5 + (double)enumFacing.getDirectionVec().getY() * 0.5, (double)blockPos.getZ() + 0.5 + (double)enumFacing.getDirectionVec().getZ() * 0.5);
                        if (!(vec3d.distanceTo(vec3d4) <= (double)this.placeRange.getValue().floatValue())) continue;
                        bl = true;
                        break;
                    }
                } else {
                    double d = 0.45;
                    double d8 = 0.05;
                    double d9 = 0.95;
                    for (double d10 = d8; d10 <= d9; d10 += d) {
                        for (double d11 = d8; d11 <= d9; d11 += d) {
                            for (double d12 = d8; d12 <= d9; d12 += d) {
                                Vec3d vec3d5 = new Vec3d((Vec3i)blockPos).add(d10, d11, d12);
                                double d13 = vec3d.distanceTo(vec3d5);
                                if (!(d13 <= (double)this.placeRange.getValue().floatValue())) continue;
                                bl = true;
                                break block27;
                            }
                        }
                    }
                }
            }
            if (!bl) {
                return false;
            }
        }
        return CrystalAura.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos2, blockPos3.add(1, 1, 1))).stream().filter(entity -> !this.breakLocations.containsKey(entity.getEntityId()) && (!(entity instanceof EntityEnderCrystal) || entity.ticksExisted > 20)).count() == 0L;
    }

    private List<EntityPlayer> getTargetsInRange() {
        List<EntityPlayer> list = CrystalAura.mc.world.playerEntities.stream().filter(entityPlayer -> entityPlayer != CrystalAura.mc.player && entityPlayer != mc.getRenderViewEntity()).filter(entityPlayer -> !entityPlayer.isDead).filter(entityPlayer -> !Experium.friendManager.isFriend(entityPlayer.getName())).filter(entityPlayer -> entityPlayer.getHealth() > 0.0f).filter(entityPlayer -> CrystalAura.mc.player.getDistance((Entity)entityPlayer) < this.enemyRange.getValue().floatValue()).sorted(Comparator.comparing(entityPlayer -> Float.valueOf(CrystalAura.mc.player.getDistance((Entity)entityPlayer)))).collect(Collectors.toList());
        if (this.targetingMode.getValue() == TargetingMode.SMART) {
            List list2 = list.stream().filter(entityPlayer -> !CrystalAura.isHole(new BlockPos((Entity)entityPlayer)) && (CrystalAura.mc.world.getBlockState(new BlockPos((Entity)entityPlayer)).getBlock() == Blocks.AIR || CrystalAura.mc.world.getBlockState(new BlockPos((Entity)entityPlayer)).getBlock() == Blocks.WEB || CrystalAura.mc.world.getBlockState(new BlockPos((Entity)entityPlayer)).getBlock() instanceof BlockLiquid)).sorted(Comparator.comparing(entityPlayer -> Float.valueOf(CrystalAura.mc.player.getDistance((Entity)entityPlayer)))).collect(Collectors.toList());
            if (list2.size() > 0) {
                list = list2;
            }
            if ((list2 = list.stream().filter(entityPlayer -> entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount() < 10.0f).sorted(Comparator.comparing(entityPlayer -> Float.valueOf(CrystalAura.mc.player.getDistance((Entity)entityPlayer)))).collect(Collectors.toList())).size() > 0) {
                list = list2;
            }
        }
        return list;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public boolean setCrystalSlot() {
        if (this.isOffhand()) {
            return true;
        }
        int n = CrystalUtil.getCrystalSlot();
        if (n == -1) {
            return false;
        }
        if (CrystalAura.mc.player.inventory.currentItem != n) {
            CrystalAura.mc.player.inventory.currentItem = n;
            CrystalAura.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(n));
        }
        return true;
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (CrystalAura.mc.world == null || CrystalAura.mc.player == null) {
            return;
        }
        Color color = new Color(this.bC.getValue().getRed(), this.bC.getValue().getGreen(), this.bC.getValue().getBlue(), this.bC.getValue().getAlpha());
        Color color2 = new Color(this.oC.getValue().getRed(), this.oC.getValue().getGreen(), this.bC.getValue().getBlue(), this.oC.getValue().getAlpha());
        if (this.renderBlock != null) {
            if (this.renderTimeoutTimer.passedMs(this.RTimerr.getValue().intValue())) {
                return;
            }
            AxisAlignedBB axisAlignedBB = null;
            try {
                axisAlignedBB = CrystalAura.mc.world.getBlockState(this.renderBlock).getBoundingBox((IBlockAccess)CrystalAura.mc.world, this.renderBlock).offset(this.renderBlock);
            }
            catch (Exception exception) {
                // empty catch block
            }
            if (axisAlignedBB == null) {
                return;
            }
            try {
                if (this.renderBlock != null) {
                    if (this.renderMode.getValue() == RenderMode.FADE) {
                        this.positions.removeIf(renderPos -> renderPos.getPos().equals((Object)this.renderBlock));
                        this.positions.add(new RenderPos(this.renderBlock, 0.0f));
                    }
                    if (this.renderMode.getValue() == RenderMode.STATIC) {
                        RenderUtil.drawSexyBox(new AxisAlignedBB(this.renderBlock), color, color2, this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.colorSync.getValue(), 1.0f, 1.0f, this.slabHeight.getValue().floatValue());
                    }
                    if (this.renderMode.getValue() == RenderMode.NEWRAINBOW) {
                        AxisAlignedBB axisAlignedBB2 = CrystalAura.mc.world.getBlockState(this.renderBlock).getSelectedBoundingBox((World)CrystalAura.mc.world, this.renderBlock);
                        Vec3d vec3d = EntityUtil.interpolateEntity((Entity)RenderUtil.mc.player, mc.getRenderPartialTicks());
                        for (EnumFacing enumFacing : EnumFacing.values()) {
                            RenderUtil.drawGradientPlaneBB(axisAlignedBB2.grow((double)0.002f).offset(-vec3d.x, -vec3d.y, -vec3d.z), enumFacing, new Color(ColorUtil.gradientRainbow(50).getRed(), ColorUtil.gradientRainbow(50).getGreen(), ColorUtil.gradientRainbow(50).getBlue(), 127), ColorUtil.invert(new Color(ColorUtil.gradientRainbow(50).getRed(), ColorUtil.gradientRainbow(50).getGreen(), ColorUtil.gradientRainbow(50).getBlue(), 127)), 2.0);
                        }
                        RenderUtil.drawGradientBlockOutline(axisAlignedBB2.grow((double)0.002f).offset(-vec3d.x, -vec3d.y, -vec3d.z), ColorUtil.invert(new Color(ColorUtil.gradientRainbow(50).getRed(), ColorUtil.gradientRainbow(50).getGreen(), ColorUtil.gradientRainbow(50).getBlue(), 255)), new Color(ColorUtil.gradientRainbow(50).getRed(), ColorUtil.gradientRainbow(50).getGreen(), ColorUtil.gradientRainbow(50).getBlue(), 255), 2.0f);
                    }
                    if (this.renderMode.getValue() == RenderMode.GLIDE) {
                        if (this.lastRenderPos == null || CrystalAura.mc.player.getDistance(this.renderBB.minX, this.renderBB.minY, this.renderBB.minZ) > 8.0) {
                            this.lastRenderPos = this.renderBlock;
                            this.renderBB = new AxisAlignedBB(this.renderBlock);
                            this.timePassed = 0.0f;
                        }
                        if (!this.lastRenderPos.equals((Object)this.renderBlock)) {
                            this.lastRenderPos = this.renderBlock;
                            this.timePassed = 0.0f;
                        }
                        double d = (double)this.renderBlock.getX() - this.renderBB.minX;
                        double d2 = (double)this.renderBlock.getY() - this.renderBB.minY;
                        double d3 = (double)this.renderBlock.getZ() - this.renderBB.minZ;
                        float f = this.timePassed / this.moveSpeed.getValue().floatValue() * this.accel.getValue().floatValue();
                        if (f > 1.0f) {
                            f = 1.0f;
                        }
                        this.renderBB = this.renderBB.offset(d * (double)f, d2 * (double)f, d3 * (double)f);
                        RenderUtil.drawSexyBox(this.renderBB, color, color2, this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.colorSync.getValue(), 1.0f, 1.0f, this.slabHeight.getValue().floatValue());
                        if (this.text.getValue().booleanValue()) {
                            RenderUtil.drawText(this.renderBB.offset(0.0, (double)(1.0f - this.slabHeight.getValue().floatValue() / 2.0f) - 0.4, 0.0), String.valueOf(new StringBuilder().append(Math.floor(this.renderDamage) == (double)this.renderDamage ? Integer.valueOf((int)this.renderDamage) : String.format("%.1f", Float.valueOf(this.renderDamage))).append("")));
                        }
                        this.timePassed = this.renderBB.equals((Object)new AxisAlignedBB(this.renderBlock)) ? 0.0f : (this.timePassed += 50.0f);
                    }
                }
                if (this.renderMode.getValue() == RenderMode.FADE) {
                    this.positions.forEach(renderPos -> {
                        float f = (this.duration.getValue().floatValue() - renderPos.getRenderTime()) / this.duration.getValue().floatValue();
                        RenderUtil.drawSexyBox(new AxisAlignedBB(renderPos.getPos()), color, color2, this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.colorSync.getValue(), this.fadeFactor.getValue() != false ? f : 1.0f, this.scaleFactor.getValue() != false ? f : 1.0f, this.slabFactor.getValue() != false ? f : 1.0f);
                        renderPos.setRenderTime(renderPos.getRenderTime() + 50.0f);
                    });
                    this.positions.removeIf(renderPos -> renderPos.getRenderTime() >= this.duration.getValue().floatValue() || CrystalAura.mc.world.isAirBlock(renderPos.getPos()) || !CrystalAura.mc.world.isAirBlock(renderPos.getPos().offset(EnumFacing.UP)));
                    if (this.positions.size() > this.max.getValue()) {
                        this.positions.remove(0);
                    }
                }
                if (this.renderBlock != null && this.text.getValue().booleanValue() && this.renderMode.getValue() != RenderMode.GLIDE) {
                    RenderUtil.drawText(new AxisAlignedBB(this.renderBlock).offset(0.0, this.renderMode.getValue() != RenderMode.FADE ? (double)(1.0f - this.slabHeight.getValue().floatValue() / 2.0f) - 0.4 : 0.1, 0.0), String.valueOf(new StringBuilder().append(Math.floor(this.renderDamage) == (double)this.renderDamage ? Integer.valueOf((int)this.renderDamage) : String.format("%.1f", Float.valueOf(this.renderDamage))).append("")));
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    private int getSwingAnimTime(EntityLivingBase entityLivingBase) {
        if (entityLivingBase.isPotionActive(MobEffects.HASTE)) {
            return 6 - (1 + entityLivingBase.getActivePotionEffect(MobEffects.HASTE).getAmplifier());
        }
        return entityLivingBase.isPotionActive(MobEffects.MINING_FATIGUE) ? 6 + (1 + entityLivingBase.getActivePotionEffect(MobEffects.MINING_FATIGUE).getAmplifier()) * 2 : 6;
    }

    public void handleBreakRotation(double d, double d2, double d3) {
        if (this.rotationMode.getValue() != RotationMode.OFF) {
            if (this.rotationMode.getValue() == RotationMode.INTERACT && this.rotationVector != null && !this.rotationTimer.passedMs(650L)) {
                if (this.rotationVector.y < d2 - 0.1) {
                    this.rotationVector = new Vec3d(this.rotationVector.x, d2, this.rotationVector.z);
                }
                this.rotations = RotationManager.calculateAngle(CrystalAura.mc.player.getPositionEyes(1.0f), this.rotationVector);
                this.rotationTimer.reset();
                return;
            }
            AxisAlignedBB axisAlignedBB = new AxisAlignedBB(d - 1.0, d2, d3 - 1.0, d + 1.0, d2 + 2.0, d3 + 1.0);
            Vec3d vec3d = new Vec3d(CrystalAura.mc.player.posX, CrystalAura.mc.player.getEntityBoundingBox().minY + (double)CrystalAura.mc.player.getEyeHeight(), CrystalAura.mc.player.posZ);
            double d4 = 0.1;
            double d5 = 0.15;
            double d6 = 0.85;
            if (axisAlignedBB.intersects(CrystalAura.mc.player.getEntityBoundingBox())) {
                d5 = 0.4;
                d6 = 0.6;
                d4 = 0.05;
            }
            Vec3d vec3d2 = null;
            double[] arrd = null;
            boolean bl = false;
            for (double d7 = d5; d7 <= d6; d7 += d4) {
                for (double d8 = d5; d8 <= d6; d8 += d4) {
                    for (double d9 = d5; d9 <= d6; d9 += d4) {
                        Vec3d vec3d3 = new Vec3d(axisAlignedBB.minX + (axisAlignedBB.maxX - axisAlignedBB.minX) * d7, axisAlignedBB.minY + (axisAlignedBB.maxY - axisAlignedBB.minY) * d8, axisAlignedBB.minZ + (axisAlignedBB.maxZ - axisAlignedBB.minZ) * d9);
                        double d10 = vec3d3.x - vec3d.x;
                        double d11 = vec3d3.y - vec3d.y;
                        double d12 = vec3d3.z - vec3d.z;
                        double[] arrd2 = new double[]{MathHelper.wrapDegrees((float)((float)Math.toDegrees(Math.atan2(d12, d10)) - 90.0f)), MathHelper.wrapDegrees((float)((float)(-Math.toDegrees(Math.atan2(d11, Math.sqrt(d10 * d10 + d12 * d12))))))};
                        boolean bl2 = true;
                        if (this.directionMode.getValue() != DirectionMode.VANILLA && !CrystalUtil.isVisible(vec3d3)) {
                            bl2 = false;
                        }
                        if (this.strictDirection.getValue().booleanValue()) {
                            if (vec3d2 != null && arrd != null) {
                                if (!bl2 && bl || !(CrystalAura.mc.player.getPositionVector().add(0.0, (double)CrystalAura.mc.player.getEyeHeight(), 0.0).distanceTo(vec3d3) < CrystalAura.mc.player.getPositionVector().add(0.0, (double)CrystalAura.mc.player.getEyeHeight(), 0.0).distanceTo(vec3d2))) continue;
                                vec3d2 = vec3d3;
                                arrd = arrd2;
                                continue;
                            }
                            vec3d2 = vec3d3;
                            arrd = arrd2;
                            bl = bl2;
                            continue;
                        }
                        if (vec3d2 != null && arrd != null) {
                            if (!bl2 && bl || !(Math.hypot(((arrd2[0] - (double)((IEntityPlayerSP)CrystalAura.mc.player).getLastReportedYaw()) % 360.0 + 540.0) % 360.0 - 180.0, arrd2[1] - (double)((IEntityPlayerSP)CrystalAura.mc.player).getLastReportedPitch()) < Math.hypot(((arrd[0] - (double)((IEntityPlayerSP)CrystalAura.mc.player).getLastReportedYaw()) % 360.0 + 540.0) % 360.0 - 180.0, arrd[1] - (double)((IEntityPlayerSP)CrystalAura.mc.player).getLastReportedPitch()))) continue;
                            vec3d2 = vec3d3;
                            arrd = arrd2;
                            continue;
                        }
                        vec3d2 = vec3d3;
                        arrd = arrd2;
                        bl = bl2;
                    }
                }
            }
            if (vec3d2 != null && arrd != null) {
                this.rotationTimer.reset();
                this.rotationVector = vec3d2;
                this.rotations = RotationManager.calculateAngle(CrystalAura.mc.player.getPositionEyes(1.0f), this.rotationVector);
            }
        }
    }

    private boolean shouldArmorBreak(EntityPlayer entityPlayer) {
        if (!this.armorBreaker.getValue().booleanValue()) {
            return false;
        }
        for (int i = 3; i >= 0; --i) {
            double d;
            ItemStack itemStack = (ItemStack)entityPlayer.inventory.armorInventory.get(i);
            if (itemStack == null || !((d = itemStack.getItem().getDurabilityForDisplay(itemStack)) > (double)this.depletion.getValue().floatValue())) continue;
            return true;
        }
        return false;
    }

    private void swingArmAfterBreaking(EnumHand enumHand) {
        if (!this.swing.getValue().booleanValue()) {
            return;
        }
        ItemStack itemStack = CrystalAura.mc.player.getHeldItem(enumHand);
        if (!itemStack.isEmpty() && itemStack.getItem().onEntitySwing((EntityLivingBase)CrystalAura.mc.player, itemStack)) {
            return;
        }
        if (!CrystalAura.mc.player.isSwingInProgress || CrystalAura.mc.player.swingProgressInt >= this.getSwingAnimTime((EntityLivingBase)CrystalAura.mc.player) / 2 || CrystalAura.mc.player.swingProgressInt < 0) {
            CrystalAura.mc.player.swingProgressInt = -1;
            CrystalAura.mc.player.isSwingInProgress = true;
            CrystalAura.mc.player.swingingHand = enumHand;
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayerPost(UpdateWalkingPlayerEvent updateWalkingPlayerEvent) {
        aboba = this.mergeOffset.getValue().floatValue() / 10.0f;
        if (updateWalkingPlayerEvent.getStage() == 1) {
            if (this.postBreakPos != null) {
                if (this.breakCrystal(this.postBreakPos)) {
                    this.breakTimer.reset();
                    this.breakLocations.put(this.postBreakPos.getEntityId(), System.currentTimeMillis());
                    for (Entity entity : CrystalAura.mc.world.loadedEntityList) {
                        if (!(entity instanceof EntityEnderCrystal) || !(entity.getDistance(this.postBreakPos.posX, this.postBreakPos.posY, this.postBreakPos.posZ) <= 6.0)) continue;
                        this.breakLocations.put(entity.getEntityId(), System.currentTimeMillis());
                    }
                    this.postBreakPos = null;
                    if (this.syncMode.getValue() == SyncMode.MERGE) {
                        this.runInstantThread();
                    }
                }
            } else if (this.postPlacePos != null) {
                if (!this.placeCrystal(this.postPlacePos, this.postFacing)) {
                    this.shouldRunThread.set(false);
                    this.postPlacePos = null;
                    return;
                }
                this.placeTimer.reset();
                this.postPlacePos = null;
            }
        }
    }

    public static void glBillboardDistanceScaled(float f, float f2, float f3, EntityPlayer entityPlayer, float f4) {
        CrystalAura.glBillboard(f, f2, f3);
        int n = (int)entityPlayer.getDistance((double)f, (double)f2, (double)f3);
        float f5 = (float)n / 2.0f / (2.0f + (2.0f - f4));
        if (f5 < 1.0f) {
            f5 = 1.0f;
        }
        GlStateManager.scale((float)f5, (float)f5, (float)f5);
    }

    private void runInstantThread() {
        if (this.mergeOffset.getValue().floatValue() == 0.0f) {
            this.doInstant();
        } else {
            this.shouldRunThread.set(true);
            if (this.thread == null || this.thread.isInterrupted() || !this.thread.isAlive()) {
                if (this.thread == null) {
                    this.thread = new Thread(InstantThread.getInstance(this));
                }
                if (this.thread != null && (this.thread.isInterrupted() || !this.thread.isAlive())) {
                    this.thread = new Thread(InstantThread.getInstance(this));
                }
                if (this.thread != null && this.thread.getState() == Thread.State.NEW) {
                    try {
                        this.thread.start();
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
            }
        }
    }

    public boolean isOffhand() {
        return CrystalAura.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
    }

    private List<Entity> getCrystalInRange() {
        return CrystalAura.mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityEnderCrystal).filter(entity -> this.isValidCrystalTarget((EntityEnderCrystal)entity)).collect(Collectors.toList());
    }

    public boolean isMoving(EntityPlayer entityPlayer) {
        return (double)entityPlayer.moveForward != 0.0 || (double)entityPlayer.moveStrafing != 0.0;
    }

    private boolean isValidCrystalTarget(EntityEnderCrystal entityEnderCrystal) {
        if (CrystalAura.mc.player.getPositionEyes(1.0f).distanceTo(entityEnderCrystal.getPositionVector()) > (double)this.breakRange.getValue().floatValue()) {
            return false;
        }
        if (this.breakLocations.containsKey(entityEnderCrystal.getEntityId()) && this.limit.getValue().booleanValue()) {
            return false;
        }
        if (this.breakLocations.containsKey(entityEnderCrystal.getEntityId()) && entityEnderCrystal.ticksExisted > this.delay.getValue() + this.attackFactor.getValue()) {
            return false;
        }
        return !(CrystalUtil.calculateDamage(entityEnderCrystal, (Entity)CrystalAura.mc.player) + this.suicideHealth.getValue().floatValue() >= CrystalAura.mc.player.getHealth() + CrystalAura.mc.player.getAbsorptionAmount());
    }

    public boolean placeCrystal(BlockPos blockPos, EnumFacing enumFacing) {
        if (blockPos != null) {
            if (this.autoSwap.getValue() != ACSwapMode.OFF && !this.hasCrystal()) {
                return false;
            }
            if (!this.isOffhand() && CrystalAura.mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL) {
                if (this.oldSlotCrystal != -1) {
                    CrystalAura.mc.player.inventory.currentItem = this.oldSlotCrystal;
                    CrystalAura.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.oldSlotCrystal));
                    this.oldSlotCrystal = -1;
                }
                return false;
            }
            if (CrystalAura.mc.world.getBlockState(blockPos.up()).getBlock() == Blocks.FIRE) {
                CrystalAura.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos.up(), EnumFacing.DOWN));
                CrystalAura.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos.up(), EnumFacing.DOWN));
                if (this.oldSlotCrystal != -1) {
                    CrystalAura.mc.player.inventory.currentItem = this.oldSlotCrystal;
                    CrystalAura.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.oldSlotCrystal));
                    this.oldSlotCrystal = -1;
                }
                return true;
            }
            this.isPlacing = true;
            if (this.postResult == null) {
                CrystalAura.rightClickBlock(blockPos, CrystalAura.mc.player.getPositionVector().add(0.0, (double)CrystalAura.mc.player.getEyeHeight(), 0.0), this.isOffhand() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, enumFacing, true);
            } else {
                CrystalAura.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(blockPos, enumFacing, this.isOffhand() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, (float)this.postResult.hitVec.x, (float)this.postResult.hitVec.y, (float)this.postResult.hitVec.z));
                CrystalAura.mc.player.connection.sendPacket((Packet)new CPacketAnimation(this.isOffhand() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));
            }
            if (this.foundDoublePop && this.renderTarget != null) {
                this.totemPops.put(this.renderTarget, new Timer());
            }
            this.isPlacing = false;
            this.placeLocations.put(blockPos, System.currentTimeMillis());
            if (this.security.getValue().floatValue() >= 0.5f) {
                this.selfPlacePositions.add(blockPos);
            }
            this.renderTimeoutTimer.reset();
            this.prevPlacePos = blockPos;
            if (this.oldSlotCrystal != -1) {
                CrystalAura.mc.player.inventory.currentItem = this.oldSlotCrystal;
                CrystalAura.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.oldSlotCrystal));
                this.oldSlotCrystal = -1;
            }
            return true;
        }
        return false;
    }

    public static void glBillboard(float f, float f2, float f3) {
        float f4 = 0.02666667f;
        GlStateManager.translate((double)((double)f - ((IRenderManager)mc.getRenderManager()).getRenderPosX()), (double)((double)f2 - ((IRenderManager)mc.getRenderManager()).getRenderPosY()), (double)((double)f3 - ((IRenderManager)mc.getRenderManager()).getRenderPosZ()));
        GlStateManager.glNormal3f((float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.rotate((float)(-Minecraft.getMinecraft().player.rotationYaw), (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.rotate((float)Minecraft.getMinecraft().player.rotationPitch, (float)(Minecraft.getMinecraft().gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f), (float)0.0f, (float)0.0f);
        GlStateManager.scale((float)(-f4), (float)(-f4), (float)f4);
    }

    private EntityEnderCrystal findCrystalTarget(List<EntityPlayer> list) {
        this.breakLocations.forEach((n, l) -> {
            if (System.currentTimeMillis() - l > 1000L) {
                this.breakLocations.remove(n);
            }
        });
        if (this.syncMode.getValue() == SyncMode.STRICT && !this.limit.getValue().booleanValue() && this.lastBroken.get()) {
            return null;
        }
        EntityEnderCrystal entityEnderCrystal = null;
        int n2 = (int)Math.max(100.0f, (float)(CrystalUtil.ping() + 50) / 1.0f) + 150;
        if (this.inhibit.getValue().booleanValue() && !this.limit.getValue().booleanValue() && !this.inhibitTimer.passedMs(n2) && this.inhibitEntity != null && CrystalAura.mc.world.getEntityByID(this.inhibitEntity.getEntityId()) != null && this.isValidCrystalTarget(this.inhibitEntity)) {
            entityEnderCrystal = this.inhibitEntity;
            return entityEnderCrystal;
        }
        List<Entity> list2 = this.getCrystalInRange();
        if (list2.isEmpty()) {
            return null;
        }
        if (this.security.getValue().floatValue() >= 1.0f) {
            double d = 0.5;
            for (Entity entity2 : list2) {
                if (!(entity2.getPositionVector().distanceTo(CrystalAura.mc.player.getPositionEyes(1.0f)) < (double)this.breakWallsRange.getValue().floatValue()) && !CrystalUtil.rayTraceBreak(entity2.posX, entity2.posY, entity2.posZ)) continue;
                EntityEnderCrystal entityEnderCrystal2 = (EntityEnderCrystal)entity2;
                double d2 = 0.0;
                for (EntityPlayer entityPlayer : list) {
                    double d3 = CrystalUtil.calculateDamage(entityEnderCrystal2, (Entity)entityPlayer);
                    d2 += d3;
                }
                double d4 = CrystalUtil.calculateDamage(entityEnderCrystal2, (Entity)CrystalAura.mc.player);
                if (d4 > d2 * (double)(this.security.getValue().floatValue() - 0.8f) && !this.selfPlacePositions.contains((Object)new BlockPos(entity2.posX, entity2.posY - 1.0, entity2.posZ)) || !(d2 > d)) continue;
                d = d2;
                entityEnderCrystal = entityEnderCrystal2;
            }
        } else {
            entityEnderCrystal = this.security.getValue().floatValue() >= 0.5f ? (EntityEnderCrystal)list2.stream().filter(entity -> this.selfPlacePositions.contains((Object)new BlockPos(entity.posX, entity.posY - 1.0, entity.posZ))).filter(entity -> entity.getPositionVector().distanceTo(CrystalAura.mc.player.getPositionEyes(1.0f)) < (double)this.breakWallsRange.getValue().floatValue() || CrystalUtil.rayTraceBreak(entity.posX, entity.posY, entity.posZ)).min(Comparator.comparing(entity -> Float.valueOf(CrystalAura.mc.player.getDistance(entity)))).orElse(null) : (EntityEnderCrystal)list2.stream().filter(entity -> entity.getPositionVector().distanceTo(CrystalAura.mc.player.getPositionEyes(1.0f)) < (double)this.breakWallsRange.getValue().floatValue() || CrystalUtil.rayTraceBreak(entity.posX, entity.posY, entity.posZ)).min(Comparator.comparing(entity -> Float.valueOf(CrystalAura.mc.player.getDistance(entity)))).orElse(null);
        }
        return entityEnderCrystal;
    }

    private double getDistance(double d, double d2, double d3, double d4, double d5, double d6) {
        double d7 = d - d4;
        double d8 = d2 - d5;
        double d9 = d3 - d6;
        return Math.sqrt(d7 * d7 + d8 * d8 + d9 * d9);
    }

    private void doInstant() {
        BlockPos blockPos;
        List<BlockPos> list;
        if (this.confirm.getValue() != ConfirmMode.OFF && (this.confirm.getValue() != ConfirmMode.FULL || this.inhibitEntity == null || (double)this.inhibitEntity.ticksExisted >= Math.floor(this.delay.getValue().intValue()))) {
            int n = (int)Math.max(100.0f, (float)(CrystalUtil.ping() + 50) / 1.0f) + 150;
            if (this.cachePos != null && !this.cacheTimer.passedMs(n + 100) && this.canPlaceCrystal(this.cachePos)) {
                this.postPlacePos = this.cachePos;
                this.postFacing = this.handlePlaceRotation(this.postPlacePos);
                if (this.postPlacePos != null) {
                    if (!this.placeCrystal(this.postPlacePos, this.postFacing)) {
                        this.postPlacePos = null;
                        return;
                    }
                    this.placeTimer.reset();
                    this.postPlacePos = null;
                }
                return;
            }
        }
        if (!(list = this.findCrystalBlocks()).isEmpty() && (blockPos = this.findPlacePosition(list, this.getTargetsInRange())) != null) {
            this.postPlacePos = blockPos;
            this.postFacing = this.handlePlaceRotation(this.postPlacePos);
            if (this.postPlacePos != null) {
                if (!this.placeCrystal(this.postPlacePos, this.postFacing)) {
                    this.postPlacePos = null;
                    return;
                }
                this.placeTimer.reset();
                this.postPlacePos = null;
            }
        }
    }

    @Override
    public void onDisable() {
        this.lastRenderPos = null;
        this.positions.clear();
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        SPacketEntityStatus sPacketEntityStatus;
        if (event.getPacket() instanceof SPacketSpawnObject) {
            SPacketSpawnObject sPacketSpawnObject = (SPacketSpawnObject)event.getPacket();
            if (sPacketSpawnObject.getType() == 51) {
                this.placeLocations.forEach((blockPos, l) -> {
                    if (this.getDistance((double)blockPos.getX() + 0.5, blockPos.getY(), (double)blockPos.getZ() + 0.5, sPacketSpawnObject.getX(), sPacketSpawnObject.getY() - 1.0, sPacketSpawnObject.getZ()) < 1.0) {
                        try {
                            this.placeLocations.remove(blockPos);
                            this.cachePos = null;
                            if (!this.limit.getValue().booleanValue() && this.inhibit.getValue().booleanValue()) {
                                this.scatterTimer.reset();
                            }
                        }
                        catch (ConcurrentModificationException concurrentModificationException) {
                            // empty catch block
                        }
                        if (this.timingMode.getValue() != TimingMode.NORMAL) {
                            return;
                        }
                        if (!this.swapTimer.passedMs((long)(this.swapDelay.getValue().floatValue() * 100.0f))) {
                            return;
                        }
                        if (this.tickRunning.get()) {
                            return;
                        }
                        if (CrystalAura.mc.player.isPotionActive(MobEffects.WEAKNESS)) {
                            return;
                        }
                        if (this.breakLocations.containsKey(sPacketSpawnObject.getEntityID())) {
                            return;
                        }
                        if (CrystalAura.mc.player.getHealth() + CrystalAura.mc.player.getAbsorptionAmount() < this.disableUnderHealth.getValue().floatValue() || this.noGapSwitch.getValue() != false && CrystalAura.mc.player.getActiveItemStack().getItem() instanceof ItemFood || this.noMineSwitch.getValue().booleanValue() && CrystalAura.mc.playerController.getIsHittingBlock() && CrystalAura.mc.player.getHeldItemMainhand().getItem() instanceof ItemTool) {
                            this.rotationVector = null;
                            return;
                        }
                        Vec3d vec3d = new Vec3d(sPacketSpawnObject.getX(), sPacketSpawnObject.getY(), sPacketSpawnObject.getZ());
                        if (CrystalAura.mc.player.getPositionEyes(1.0f).distanceTo(vec3d) > (double)this.breakRange.getValue().floatValue()) {
                            return;
                        }
                        if (!this.breakTimer.passedMs((long)(1000.0f - this.breakSpeed.getValue().floatValue() * 50.0f))) {
                            return;
                        }
                        if (CrystalUtil.calculateDamage(sPacketSpawnObject.getX(), sPacketSpawnObject.getY(), sPacketSpawnObject.getZ(), (Entity)CrystalAura.mc.player) + this.suicideHealth.getValue().floatValue() >= CrystalAura.mc.player.getHealth() + CrystalAura.mc.player.getAbsorptionAmount()) {
                            return;
                        }
                        this.breakLocations.put(sPacketSpawnObject.getEntityID(), System.currentTimeMillis());
                        this.bilateralVec = new Vec3d(sPacketSpawnObject.getX(), sPacketSpawnObject.getY(), sPacketSpawnObject.getZ());
                        CPacketUseEntity cPacketUseEntity = new CPacketUseEntity();
                        ((ICPacketUseEntity)cPacketUseEntity).setEntityId(sPacketSpawnObject.getEntityID());
                        ((ICPacketUseEntity)cPacketUseEntity).setAction(CPacketUseEntity.Action.ATTACK);
                        CrystalAura.mc.player.connection.sendPacket((Packet)new CPacketAnimation(this.isOffhand() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));
                        CrystalAura.mc.player.connection.sendPacket((Packet)cPacketUseEntity);
                        this.swingArmAfterBreaking(this.isOffhand() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                        this.renderBreakingPos = new BlockPos(sPacketSpawnObject.getX(), sPacketSpawnObject.getY() - 1.0, sPacketSpawnObject.getZ());
                        this.renderBreakingTimer.reset();
                        this.breakTimer.reset();
                        this.linearTimer.reset();
                        if (this.syncMode.getValue() == SyncMode.MERGE) {
                            this.placeTimer.reset();
                        }
                        if (this.syncMode.getValue() == SyncMode.STRICT) {
                            this.lastBroken.set(true);
                        }
                        if (this.syncMode.getValue() == SyncMode.MERGE) {
                            this.runInstantThread();
                        }
                    }
                });
            }
        } else if (event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect sPacketSoundEffect = (SPacketSoundEffect)event.getPacket();
            if (sPacketSoundEffect.getCategory() == SoundCategory.BLOCKS && sPacketSoundEffect.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                if (this.inhibitEntity != null && this.inhibitEntity.getDistance(sPacketSoundEffect.getX(), sPacketSoundEffect.getY(), sPacketSoundEffect.getZ()) < 6.0) {
                    this.inhibitEntity = null;
                }
                if (this.security.getValue().floatValue() >= 0.5f) {
                    try {
                        this.selfPlacePositions.remove((Object)new BlockPos(sPacketSoundEffect.getX(), sPacketSoundEffect.getY() - 1.0, sPacketSoundEffect.getZ()));
                    }
                    catch (ConcurrentModificationException concurrentModificationException) {}
                }
            }
        } else if (event.getPacket() instanceof SPacketEntityStatus && (sPacketEntityStatus = (SPacketEntityStatus)event.getPacket()).getOpCode() == 35 && sPacketEntityStatus.getEntity((World)CrystalAura.mc.world) instanceof EntityPlayer) {
            this.totemPops.put((EntityPlayer)sPacketEntityStatus.getEntity((World)CrystalAura.mc.world), new Timer());
        }
    }

    public static List<BlockPos> getSphere(BlockPos blockPos, float f, int n, boolean bl, boolean bl2, int n2) {
        ArrayList<BlockPos> arrayList = new ArrayList<BlockPos>();
        int n3 = blockPos.getX();
        int n4 = blockPos.getY();
        int n5 = blockPos.getZ();
        int n6 = n3 - (int)f;
        while ((float)n6 <= (float)n3 + f) {
            int n7 = n5 - (int)f;
            while ((float)n7 <= (float)n5 + f) {
                int n8 = bl2 ? n4 - (int)f : n4;
                while (true) {
                    float f2;
                    float f3 = n8;
                    float f4 = f2 = bl2 ? (float)n4 + f : (float)(n4 + n);
                    if (!(f3 < f2)) break;
                    double d = (n3 - n6) * (n3 - n6) + (n5 - n7) * (n5 - n7) + (bl2 ? (n4 - n8) * (n4 - n8) : 0);
                    if (!(!(d < (double)(f * f)) || bl && d < (double)((f - 1.0f) * (f - 1.0f)))) {
                        BlockPos blockPos2 = new BlockPos(n6, n8 + n2, n7);
                        arrayList.add(blockPos2);
                    }
                    ++n8;
                }
                ++n7;
            }
            ++n6;
        }
        return arrayList;
    }

    @SubscribeEvent
    public void onChangeItem(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketHeldItemChange) {
            this.swapTimer.reset();
        }
    }

    public EntityEnderCrystal getPostBreakPos() {
        return this.postBreakPos;
    }

    private boolean isDoublePoppable(EntityPlayer entityPlayer, float f) {
        if (this.predictPops.getValue().booleanValue() && entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount() <= 2.0f && (double)f > (double)entityPlayer.getHealth() + (double)entityPlayer.getAbsorptionAmount() + 0.5 && f <= 4.0f) {
            Timer timer = this.totemPops.get((Object)entityPlayer);
            return timer == null || timer.passedMs(500L);
        }
        return false;
    }

    public boolean switchToSword() {
        int n = CrystalUtil.getSwordSlot();
        if (CrystalAura.mc.player.inventory.currentItem != n && n != -1) {
            if (this.antiWeakness.getValue() == ACAntiWeakness.SILENT) {
                this.oldSlotSword = CrystalAura.mc.player.inventory.currentItem;
            }
            CrystalAura.mc.player.inventory.currentItem = n;
            CrystalAura.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(n));
        }
        return n != -1;
    }

    private List<BlockPos> findCrystalBlocks() {
        NonNullList nonNullList = NonNullList.create();
        nonNullList.addAll((Collection)CrystalAura.getSphere(new BlockPos((Entity)CrystalAura.mc.player), this.strictDirection.getValue() != false ? this.placeRange.getValue().floatValue() + 2.0f : this.placeRange.getValue().floatValue(), this.placeRange.getValue().intValue(), false, true, 0).stream().filter(this::canPlaceCrystal).collect(Collectors.toList()));
        return nonNullList;
    }

    public boolean hasCrystal() {
        if (this.isOffhand()) {
            return true;
        }
        int n = CrystalUtil.getCrystalSlot();
        if (n == -1) {
            return false;
        }
        if (CrystalAura.mc.player.inventory.currentItem == n) {
            return true;
        }
        if (this.autoSwap.getValue() == ACSwapMode.SILENT) {
            this.oldSlotCrystal = CrystalAura.mc.player.inventory.currentItem;
        }
        CrystalAura.mc.player.inventory.currentItem = n;
        CrystalAura.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(n));
        return true;
    }
	
	public static boolean isHole(BlockPos blockPos) {
        return CrystalAura.validObi(blockPos) || CrystalAura.validBedrock(blockPos);
    }
	
	public static boolean validBedrock(BlockPos blockPos) {
        return CrystalAura.mc.world.getBlockState(blockPos.add(0, -1, 0)).getBlock() == Blocks.BEDROCK && CrystalAura.mc.world.getBlockState(blockPos.add(1, 0, 0)).getBlock() == Blocks.BEDROCK && CrystalAura.mc.world.getBlockState(blockPos.add(-1, 0, 0)).getBlock() == Blocks.BEDROCK && CrystalAura.mc.world.getBlockState(blockPos.add(0, 0, 1)).getBlock() == Blocks.BEDROCK && CrystalAura.mc.world.getBlockState(blockPos.add(0, 0, -1)).getBlock() == Blocks.BEDROCK && CrystalAura.mc.world.getBlockState(blockPos).getMaterial() == Material.AIR && CrystalAura.mc.world.getBlockState(blockPos.add(0, 1, 0)).getMaterial() == Material.AIR && CrystalAura.mc.world.getBlockState(blockPos.add(0, 2, 0)).getMaterial() == Material.AIR;
    }
	
	public static boolean validObi(BlockPos blockPos) {
        return !(CrystalAura.validBedrock(blockPos) || CrystalAura.mc.world.getBlockState(blockPos.add(0, -1, 0)).getBlock() != Blocks.OBSIDIAN && CrystalAura.mc.world.getBlockState(blockPos.add(0, -1, 0)).getBlock() != Blocks.BEDROCK || CrystalAura.mc.world.getBlockState(blockPos.add(1, 0, 0)).getBlock() != Blocks.OBSIDIAN && CrystalAura.mc.world.getBlockState(blockPos.add(1, 0, 0)).getBlock() != Blocks.BEDROCK || CrystalAura.mc.world.getBlockState(blockPos.add(-1, 0, 0)).getBlock() != Blocks.OBSIDIAN && CrystalAura.mc.world.getBlockState(blockPos.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK || CrystalAura.mc.world.getBlockState(blockPos.add(0, 0, 1)).getBlock() != Blocks.OBSIDIAN && CrystalAura.mc.world.getBlockState(blockPos.add(0, 0, 1)).getBlock() != Blocks.BEDROCK || CrystalAura.mc.world.getBlockState(blockPos.add(0, 0, -1)).getBlock() != Blocks.OBSIDIAN && CrystalAura.mc.world.getBlockState(blockPos.add(0, 0, -1)).getBlock() != Blocks.BEDROCK || CrystalAura.mc.world.getBlockState(blockPos).getMaterial() != Material.AIR || CrystalAura.mc.world.getBlockState(blockPos.add(0, 1, 0)).getMaterial() != Material.AIR || CrystalAura.mc.world.getBlockState(blockPos.add(0, 2, 0)).getMaterial() != Material.AIR);
    }
	
	public static void setPlayerRotations(float f, float f2) {
        CrystalAura.mc.player.rotationYaw = f;
        CrystalAura.mc.player.rotationYawHead = f;
        CrystalAura.mc.player.rotationPitch = f2;
    }
	
	public static void lookAtAngles(float f, float f2) {
        CrystalAura.setPlayerRotations(f, f2);
        CrystalAura.mc.player.rotationYawHead = f;
    }
	
	public static void rightClickBlock(BlockPos blockPos, Vec3d vec3d, EnumHand enumHand, EnumFacing enumFacing, boolean bl) {
        if (bl) {
            float f = (float)(vec3d.x - (double)blockPos.getX());
            float f2 = (float)(vec3d.y - (double)blockPos.getY());
            float f3 = (float)(vec3d.z - (double)blockPos.getZ());
            CrystalAura.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(blockPos, enumFacing, enumHand, f, f2, f3));
            CrystalAura.mc.player.connection.sendPacket((Packet)new CPacketAnimation(enumHand));
        } else {
            CrystalAura.mc.playerController.processRightClickBlock(CrystalAura.mc.player, CrystalAura.mc.world, blockPos, enumFacing, vec3d, enumHand);
            CrystalAura.mc.player.swingArm(enumHand);
        }
    }


    private static enum TimingMode {
        SEQUENTIAL,
        NORMAL;
    }

    private static enum RenderMode {
        STATIC,
        FADE,
        GLIDE,
        NEWRAINBOW;
    }

    private static enum YawStepMode {
        OFF,
        SEMI,
        FULL;
    }

    private static enum TargetingMode {
        ALL,
        SMART,
        NEAREST;
    }

    public static enum ACSwapMode {
        OFF,
        NORMAL,
        SILENT;
    }

    private static enum RotationMode {
        OFF,
        TRACK,
        INTERACT;
    }

    public static enum ConfirmMode {
        OFF,
        SEMI,
        FULL;
    }

    public static enum Pages {
        General,
        Place,
        Break,
        Calculation,
        Render;
    }

    private static class InstantThread
    implements Runnable {
        private CrystalAura CrystalAura;
        private static InstantThread INSTANCE;

        private InstantThread() {
        }

        static InstantThread getInstance(CrystalAura crystalAura) {
            if (INSTANCE == null) {
                INSTANCE = new InstantThread();
                InstantThread.INSTANCE.CrystalAura = crystalAura;
            }
            return INSTANCE;
        }
        @Override
        public void run() {
            if (this.CrystalAura.shouldRunThread.get()) {
                try {
                    Thread.sleep((long)(aboba * 40.0f));
                }
                catch (InterruptedException interruptedException) {
                    this.CrystalAura.thread.interrupt();
                }
                if (!this.CrystalAura.shouldRunThread.get()) {
                    return;
                }
                this.CrystalAura.shouldRunThread.set(false);
                if (this.CrystalAura.tickRunning.get()) {
                    return;
                }
                this.CrystalAura.doInstant();
            }
        }
    }

    public static enum SyncMode {
        STRICT,
        MERGE;
    }

    private class RenderPos {
        private BlockPos renderPos;
        private float renderTime;
        public void setRenderTime(float f) {
            this.renderTime = f;
        }
        public BlockPos getPos() {
            return this.renderPos;
        }
        public void setPos(BlockPos blockPos) {
            this.renderPos = blockPos;
        }
        public RenderPos(BlockPos blockPos, float f) {
            this.renderPos = blockPos;
            this.renderTime = f;
        }
        public float getRenderTime() {
            return this.renderTime;
        }
    }

    public static enum DirectionMode {
        VANILLA,
        NORMAL,
        STRICT;
    }

    public static enum ACAntiWeakness {
        OFF,
        NORMAL,
        SILENT;
    }
}
