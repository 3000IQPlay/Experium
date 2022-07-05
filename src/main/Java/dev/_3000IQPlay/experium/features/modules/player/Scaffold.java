package dev._3000IQPlay.experium.features.modules.player;

import java.util.Arrays;
import java.util.List;
import dev._3000IQPlay.experium.event.events.UpdateWalkingPlayerEvent;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.util.BlockUtil;
import dev._3000IQPlay.experium.util.EntityUtil;
import dev._3000IQPlay.experium.util.InventoryUtil;
import dev._3000IQPlay.experium.util.MathUtil;
import dev._3000IQPlay.experium.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Scaffold
        extends Module {
    private final Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.New));
    private final Setting<Boolean> swing = this.register(new Setting<Boolean>("SwingArm", Boolean.valueOf(true), bl -> this.mode.getValue() == Mode.New));
    private final Setting<Boolean> bSwitch = this.register(new Setting<Boolean>("Switch", Boolean.valueOf(true), bl -> this.mode.getValue() == Mode.New));
    private final Setting<Boolean> center = this.register(new Setting<Boolean>("Center", Boolean.valueOf(false), bl -> this.mode.getValue() == Mode.New));
    private final Setting<Boolean> tower = this.register(new Setting<Boolean>("Tower", Boolean.valueOf(true), bl -> this.mode.getValue() == Mode.New));
    private final Setting<Boolean> keepY = this.register(new Setting<Boolean>("KeepYLevel", Boolean.valueOf(false), bl -> this.mode.getValue() == Mode.New));
    private final Setting<Boolean> sprint = this.register(new Setting<Boolean>("UseSprint", Boolean.valueOf(true), bl -> this.mode.getValue() == Mode.New));
    private final Setting<Boolean> replenishBlocks = this.register(new Setting<Boolean>("Replenish", Boolean.valueOf(true), bl -> this.mode.getValue() == Mode.New));
    private final Setting<Boolean> down = this.register(new Setting<Boolean>("Down", Boolean.valueOf(true), bl -> this.mode.getValue() == Mode.New));
    private final Setting<Float> expand = this.register(new Setting<Float>("Expand", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(6.0f), f -> this.mode.getValue() == Mode.New));
    private final List<Block> invalid = Arrays.asList(Blocks.ENCHANTING_TABLE, Blocks.FURNACE, Blocks.CARPET, Blocks.CRAFTING_TABLE, Blocks.TRAPPED_CHEST, Blocks.CHEST, Blocks.DISPENSER, Blocks.AIR, Blocks.WATER, Blocks.LAVA, Blocks.FLOWING_WATER, Blocks.FLOWING_LAVA, Blocks.SNOW_LAYER, Blocks.TORCH, Blocks.ANVIL, Blocks.JUKEBOX, Blocks.STONE_BUTTON, Blocks.WOODEN_BUTTON, Blocks.LEVER, Blocks.NOTEBLOCK, Blocks.STONE_PRESSURE_PLATE, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Blocks.WOODEN_PRESSURE_PLATE, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM, Blocks.YELLOW_FLOWER, Blocks.RED_FLOWER, Blocks.ANVIL, Blocks.CACTUS, Blocks.LADDER, Blocks.ENDER_CHEST);
    private final Timer timerMotion = new Timer();
    private final Timer itemTimer = new Timer();
    private final Timer timer = new Timer();
    public Setting<Boolean> rotation = this.register(new Setting<Boolean>("Rotate", Boolean.valueOf(false), bl -> this.mode.getValue() == Mode.Old));
    private int lastY;
    private BlockPos pos;
    private boolean teleported;

    public Scaffold() {
        super("Scaffold", "Places Blocks underneath you.", Module.Category.PLAYER, true, false, false);
    }

    public static void swap(int n, int n2) {
        Scaffold.mc.playerController.windowClick(Scaffold.mc.player.inventoryContainer.windowId, n, 0, ClickType.PICKUP, (EntityPlayer)Scaffold.mc.player);
        Scaffold.mc.playerController.windowClick(Scaffold.mc.player.inventoryContainer.windowId, n2, 0, ClickType.PICKUP, (EntityPlayer)Scaffold.mc.player);
        Scaffold.mc.playerController.windowClick(Scaffold.mc.player.inventoryContainer.windowId, n, 0, ClickType.PICKUP, (EntityPlayer)Scaffold.mc.player);
        Scaffold.mc.playerController.updateController();
    }

    public static int getItemSlot(Container container, Item item) {
        int n = 0;
        for (int i = 9; i < 45; ++i) {
            if (!container.getSlot(i).getHasStack() || container.getSlot(i).getStack().getItem() != item) continue;
            n = i;
        }
        return n;
    }

    public static boolean isMoving(EntityLivingBase entityLivingBase) {
        return entityLivingBase.moveForward == 0.0f && entityLivingBase.moveStrafing == 0.0f;
    }

    @Override
    public void onEnable() {
        this.timer.reset();
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayerPost(UpdateWalkingPlayerEvent updateWalkingPlayerEvent) {
        if (this.mode.getValue() == Mode.Old) {
            BlockPos blockPos;
            if (this.isOff() || Scaffold.fullNullCheck() || updateWalkingPlayerEvent.getStage() == 0) {
                return;
            }
            if (!Scaffold.mc.gameSettings.keyBindJump.isKeyDown()) {
                this.timer.reset();
            }
            if (BlockUtil.isScaffoldPos((blockPos = EntityUtil.getPlayerPosWithEntity()).add(0, -1, 0))) {
                if (BlockUtil.isValidBlock(blockPos.add(0, -2, 0))) {
                    this.place(blockPos.add(0, -1, 0), EnumFacing.UP);
                } else if (BlockUtil.isValidBlock(blockPos.add(-1, -1, 0))) {
                    this.place(blockPos.add(0, -1, 0), EnumFacing.EAST);
                } else if (BlockUtil.isValidBlock(blockPos.add(1, -1, 0))) {
                    this.place(blockPos.add(0, -1, 0), EnumFacing.WEST);
                } else if (BlockUtil.isValidBlock(blockPos.add(0, -1, -1))) {
                    this.place(blockPos.add(0, -1, 0), EnumFacing.SOUTH);
                } else if (BlockUtil.isValidBlock(blockPos.add(0, -1, 1))) {
                    this.place(blockPos.add(0, -1, 0), EnumFacing.NORTH);
                } else if (BlockUtil.isValidBlock(blockPos.add(1, -1, 1))) {
                    if (BlockUtil.isValidBlock(blockPos.add(0, -1, 1))) {
                        this.place(blockPos.add(0, -1, 1), EnumFacing.NORTH);
                    }
                    this.place(blockPos.add(1, -1, 1), EnumFacing.EAST);
                } else if (BlockUtil.isValidBlock(blockPos.add(-1, -1, 1))) {
                    if (BlockUtil.isValidBlock(blockPos.add(-1, -1, 0))) {
                        this.place(blockPos.add(0, -1, 1), EnumFacing.WEST);
                    }
                    this.place(blockPos.add(-1, -1, 1), EnumFacing.SOUTH);
                } else if (BlockUtil.isValidBlock(blockPos.add(1, -1, 1))) {
                    if (BlockUtil.isValidBlock(blockPos.add(0, -1, 1))) {
                        this.place(blockPos.add(0, -1, 1), EnumFacing.SOUTH);
                    }
                    this.place(blockPos.add(1, -1, 1), EnumFacing.WEST);
                } else if (BlockUtil.isValidBlock(blockPos.add(1, -1, 1))) {
                    if (BlockUtil.isValidBlock(blockPos.add(0, -1, 1))) {
                        this.place(blockPos.add(0, -1, 1), EnumFacing.EAST);
                    }
                    this.place(blockPos.add(1, -1, 1), EnumFacing.NORTH);
                }
            }
        }
    }

    @Override
    public void onUpdate() {
        if (this.mode.getValue() == Mode.New) {
            if (this.down.getValue().booleanValue() && Scaffold.mc.gameSettings.keyBindSneak.isKeyDown() && !this.sprint.getValue().booleanValue()) {
                Scaffold.mc.player.setSprinting(false);
            }
            if (this.replenishBlocks.getValue().booleanValue() && !(Scaffold.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBlock) && this.getBlockCountHotbar() <= 0 && this.itemTimer.passedMs(100L)) {
                for (int i = 9; i < 45; ++i) {
                    ItemStack itemStack;
                    if (!Scaffold.mc.player.inventoryContainer.getSlot(i).getHasStack() || !((itemStack = Scaffold.mc.player.inventoryContainer.getSlot(i).getStack()).getItem() instanceof ItemBlock) || this.invalid.contains(Block.getBlockFromItem((Item)itemStack.getItem())) || i >= 36) continue;
                    Scaffold.swap(Scaffold.getItemSlot(Scaffold.mc.player.inventoryContainer, itemStack.getItem()), 44);
                }
            }
            if (this.keepY.getValue().booleanValue()) {
                if (Scaffold.isMoving((EntityLivingBase)Scaffold.mc.player) && Scaffold.mc.gameSettings.keyBindJump.isKeyDown() || Scaffold.mc.player.collidedVertically || Scaffold.mc.player.onGround) {
                    this.lastY = MathHelper.floor((double)Scaffold.mc.player.posY);
                }
            } else {
                this.lastY = MathHelper.floor((double)Scaffold.mc.player.posY);
            }
            BlockData blockData = null;
            double d = Scaffold.mc.player.posX;
            double d2 = Scaffold.mc.player.posZ;
            double d3 = this.keepY.getValue() != false ? (double)this.lastY : Scaffold.mc.player.posY;
            double d4 = Scaffold.mc.player.movementInput.moveForward;
            double d5 = Scaffold.mc.player.movementInput.moveStrafe;
            float f = Scaffold.mc.player.rotationYaw;
            if (!Scaffold.mc.player.collidedHorizontally) {
                double[] object2 = this.getExpandCoords(d, d2, d4, d5, f);
                d = object2[0];
                d2 = object2[1];
            }
            if (this.canPlace(Scaffold.mc.world.getBlockState(new BlockPos(Scaffold.mc.player.posX, Scaffold.mc.player.posY - (double)(Scaffold.mc.gameSettings.keyBindSneak.isKeyDown() && this.down.getValue() != false ? 2 : 1), Scaffold.mc.player.posZ)).getBlock())) {
                d = Scaffold.mc.player.posX;
                d2 = Scaffold.mc.player.posZ;
            }
            BlockPos object = new BlockPos(d, d3 - 1.0, d2);
            if (Scaffold.mc.gameSettings.keyBindSneak.isKeyDown() && this.down.getValue().booleanValue()) {
                object = new BlockPos(d, d3 - 2.0, d2);
            }
            this.pos = object;
            if (Scaffold.mc.world.getBlockState(object).getBlock() == Blocks.AIR) {
                blockData = this.getBlockData2(object);
            }
            if (blockData != null) {
                if (this.getBlockCountHotbar() <= 0 || !this.bSwitch.getValue().booleanValue() && !(Scaffold.mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock)) {
                    return;
                }
                int n = Scaffold.mc.player.inventory.currentItem;
                if (this.bSwitch.getValue().booleanValue()) {
                    for (int i = 0; i < 9; ++i) {
                        Scaffold.mc.player.inventory.getStackInSlot(i);
                        if (Scaffold.mc.player.inventory.getStackInSlot(i).getCount() == 0 || !(Scaffold.mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock) || this.invalid.contains(((ItemBlock)Scaffold.mc.player.inventory.getStackInSlot(i).getItem()).getBlock())) continue;
                        Scaffold.mc.player.inventory.currentItem = i;
                        break;
                    }
                }
                if (this.mode.getValue() == Mode.New) {
                    if (Scaffold.mc.gameSettings.keyBindJump.isKeyDown() && Scaffold.mc.player.moveForward == 0.0f && Scaffold.mc.player.moveStrafing == 0.0f && !Scaffold.mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                        if (!this.teleported && this.center.getValue().booleanValue()) {
                            this.teleported = true;
                            BlockPos blockPos = new BlockPos(Scaffold.mc.player.posX, Scaffold.mc.player.posY, Scaffold.mc.player.posZ);
                            Scaffold.mc.player.setPosition((double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5);
                        }
                        if (this.center.getValue().booleanValue() && !this.teleported) {
                            return;
                        }
                        Scaffold.mc.player.motionY = 0.42f;
                        Scaffold.mc.player.motionZ = 0.0;
                        Scaffold.mc.player.motionX = 0.0;
                        if (!this.tower.getValue().booleanValue()) {
                            Scaffold.mc.player.motionY = -0.28;
                        }
                    } else {
                        this.timerMotion.reset();
                        if (this.teleported && this.center.getValue().booleanValue()) {
                            this.teleported = false;
                        }
                    }
                }
                if (Scaffold.mc.playerController.processRightClickBlock(Scaffold.mc.player, Scaffold.mc.world, blockData.position, blockData.face, new Vec3d((double)blockData.position.getX() + Math.random(), (double)blockData.position.getY() + Math.random(), (double)blockData.position.getZ() + Math.random()), EnumHand.MAIN_HAND) != EnumActionResult.FAIL) {
                    if (this.swing.getValue().booleanValue()) {
                        Scaffold.mc.player.swingArm(EnumHand.MAIN_HAND);
                    } else {
                        Scaffold.mc.player.connection.sendPacket((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
                    }
                }
                Scaffold.mc.player.inventory.currentItem = n;
            }
        }
    }

    public double[] getExpandCoords(double x, double z, double forward, double strafe, float YAW) {
        BlockPos underPos = new BlockPos ( x , mc.player.posY - (double) ( mc.gameSettings.keyBindSneak.isKeyDown ( ) && (Boolean) this.down.getValue ( ) ? 2 : 1 ) , z );
        Block underBlock = mc.world.getBlockState ( underPos ).getBlock ( );
        double xCalc = - 999.0D;
        double zCalc = - 999.0D;
        double dist = 0.0D;

        for (double expandDist = (Float) this.expand.getValue ( ) * 2.0F; ! this.canPlace ( underBlock ); underBlock = mc.world.getBlockState ( underPos ).getBlock ( )) {
            ++ dist;
            if ( dist > expandDist ) {
                dist = expandDist;
            }

            xCalc = x + ( forward * 0.45D * Math.cos ( Math.toRadians ( YAW + 90.0F ) ) + strafe * 0.45D * Math.sin ( Math.toRadians ( YAW + 90.0F ) ) ) * dist;
            zCalc = z + ( forward * 0.45D * Math.sin ( Math.toRadians ( YAW + 90.0F ) ) - strafe * 0.45D * Math.cos ( Math.toRadians ( YAW + 90.0F ) ) ) * dist;
            if ( dist == expandDist ) {
                break;
            }

            underPos = new BlockPos ( xCalc , mc.player.posY - (double) ( mc.gameSettings.keyBindSneak.isKeyDown ( ) && (Boolean) this.down.getValue ( ) ? 2 : 1 ) , zCalc );
        }

        return new double[]{xCalc , zCalc};
    }

    public boolean canPlace(Block block) {
        return (block instanceof BlockAir || block instanceof BlockLiquid) && Scaffold.mc.world != null && Scaffold.mc.player != null && this.pos != null && Scaffold.mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(this.pos)).isEmpty();
    }

    private int getBlockCountHotbar() {
        int n = 0;
        for (int i = 36; i < 45; ++i) {
            if (!Scaffold.mc.player.inventoryContainer.getSlot(i).getHasStack()) continue;
            ItemStack itemStack = Scaffold.mc.player.inventoryContainer.getSlot(i).getStack();
            Item item = itemStack.getItem();
            if (!(itemStack.getItem() instanceof ItemBlock) || this.invalid.contains(((ItemBlock)item).getBlock())) continue;
            n += itemStack.getCount();
        }
        return n;
    }

    private BlockData getBlockData2(BlockPos blockPos) {
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos.add(0, -1, 0)).getBlock())) {
            return new BlockData(blockPos.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos.add(-1, 0, 0)).getBlock())) {
            return new BlockData(blockPos.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos.add(1, 0, 0)).getBlock())) {
            return new BlockData(blockPos.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos.add(0, 0, 1)).getBlock())) {
            return new BlockData(blockPos.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos.add(0, 0, -1)).getBlock())) {
            return new BlockData(blockPos.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos.add(0, 1, 0)).getBlock())) {
            return new BlockData(blockPos.add(0, 1, 0), EnumFacing.DOWN);
        }
        BlockPos blockPos2 = blockPos.add(-1, 0, 0);
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos2.add(0, -1, 0)).getBlock())) {
            return new BlockData(blockPos2.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos2.add(0, 1, 0)).getBlock())) {
            return new BlockData(blockPos2.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos2.add(-1, 0, 0)).getBlock())) {
            return new BlockData(blockPos2.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos2.add(1, 0, 0)).getBlock())) {
            return new BlockData(blockPos2.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos2.add(0, 0, 1)).getBlock())) {
            return new BlockData(blockPos2.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos2.add(0, 0, -1)).getBlock())) {
            return new BlockData(blockPos2.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos blockPos3 = blockPos.add(1, 0, 0);
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos3.add(0, -1, 0)).getBlock())) {
            return new BlockData(blockPos3.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos3.add(0, 1, 0)).getBlock())) {
            return new BlockData(blockPos3.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos3.add(-1, 0, 0)).getBlock())) {
            return new BlockData(blockPos3.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos3.add(1, 0, 0)).getBlock())) {
            return new BlockData(blockPos3.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos3.add(0, 0, 1)).getBlock())) {
            return new BlockData(blockPos3.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos3.add(0, 0, -1)).getBlock())) {
            return new BlockData(blockPos3.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos blockPos4 = blockPos.add(0, 0, 1);
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos4.add(0, -1, 0)).getBlock())) {
            return new BlockData(blockPos4.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos4.add(0, 1, 0)).getBlock())) {
            return new BlockData(blockPos4.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos4.add(-1, 0, 0)).getBlock())) {
            return new BlockData(blockPos4.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos4.add(1, 0, 0)).getBlock())) {
            return new BlockData(blockPos4.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos4.add(0, 0, 1)).getBlock())) {
            return new BlockData(blockPos4.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos4.add(0, 0, -1)).getBlock())) {
            return new BlockData(blockPos4.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos blockPos5 = blockPos.add(0, 0, -1);
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos5.add(0, -1, 0)).getBlock())) {
            return new BlockData(blockPos5.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos5.add(0, 1, 0)).getBlock())) {
            return new BlockData(blockPos5.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos5.add(-1, 0, 0)).getBlock())) {
            return new BlockData(blockPos5.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos5.add(1, 0, 0)).getBlock())) {
            return new BlockData(blockPos5.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos5.add(0, 0, 1)).getBlock())) {
            return new BlockData(blockPos5.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos5.add(0, 0, -1)).getBlock())) {
            return new BlockData(blockPos5.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos2.add(0, -1, 0)).getBlock())) {
            return new BlockData(blockPos2.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos2.add(0, 1, 0)).getBlock())) {
            return new BlockData(blockPos2.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos2.add(-1, 0, 0)).getBlock())) {
            return new BlockData(blockPos2.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos2.add(1, 0, 0)).getBlock())) {
            return new BlockData(blockPos2.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos2.add(0, 0, 1)).getBlock())) {
            return new BlockData(blockPos2.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos2.add(0, 0, -1)).getBlock())) {
            return new BlockData(blockPos2.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos3.add(0, -1, 0)).getBlock())) {
            return new BlockData(blockPos3.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos3.add(0, 1, 0)).getBlock())) {
            return new BlockData(blockPos3.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos3.add(-1, 0, 0)).getBlock())) {
            return new BlockData(blockPos3.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos3.add(1, 0, 0)).getBlock())) {
            return new BlockData(blockPos3.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos3.add(0, 0, 1)).getBlock())) {
            return new BlockData(blockPos3.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos3.add(0, 0, -1)).getBlock())) {
            return new BlockData(blockPos3.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos4.add(0, -1, 0)).getBlock())) {
            return new BlockData(blockPos4.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos4.add(0, 1, 0)).getBlock())) {
            return new BlockData(blockPos4.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos4.add(-1, 0, 0)).getBlock())) {
            return new BlockData(blockPos4.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos4.add(1, 0, 0)).getBlock())) {
            return new BlockData(blockPos4.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos4.add(0, 0, 1)).getBlock())) {
            return new BlockData(blockPos4.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos4.add(0, 0, -1)).getBlock())) {
            return new BlockData(blockPos4.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos5.add(0, -1, 0)).getBlock())) {
            return new BlockData(blockPos5.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos5.add(0, 1, 0)).getBlock())) {
            return new BlockData(blockPos5.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos5.add(-1, 0, 0)).getBlock())) {
            return new BlockData(blockPos5.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos5.add(1, 0, 0)).getBlock())) {
            return new BlockData(blockPos5.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos5.add(0, 0, 1)).getBlock())) {
            return new BlockData(blockPos5.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos5.add(0, 0, -1)).getBlock())) {
            return new BlockData(blockPos5.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos blockPos10 = blockPos.add(0, -1, 0);
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos10.add(0, -1, 0)).getBlock())) {
            return new BlockData(blockPos10.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos10.add(0, 1, 0)).getBlock())) {
            return new BlockData(blockPos10.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos10.add(-1, 0, 0)).getBlock())) {
            return new BlockData(blockPos10.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos10.add(1, 0, 0)).getBlock())) {
            return new BlockData(blockPos10.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos10.add(0, 0, 1)).getBlock())) {
            return new BlockData(blockPos10.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos10.add(0, 0, -1)).getBlock())) {
            return new BlockData(blockPos10.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos blockPos11 = blockPos10.add(1, 0, 0);
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos11.add(0, -1, 0)).getBlock())) {
            return new BlockData(blockPos11.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos11.add(0, 1, 0)).getBlock())) {
            return new BlockData(blockPos11.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos11.add(-1, 0, 0)).getBlock())) {
            return new BlockData(blockPos11.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos11.add(1, 0, 0)).getBlock())) {
            return new BlockData(blockPos11.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos11.add(0, 0, 1)).getBlock())) {
            return new BlockData(blockPos11.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos11.add(0, 0, -1)).getBlock())) {
            return new BlockData(blockPos11.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos blockPos12 = blockPos10.add(-1, 0, 0);
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos12.add(0, -1, 0)).getBlock())) {
            return new BlockData(blockPos12.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos12.add(0, 1, 0)).getBlock())) {
            return new BlockData(blockPos12.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos12.add(-1, 0, 0)).getBlock())) {
            return new BlockData(blockPos12.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos12.add(1, 0, 0)).getBlock())) {
            return new BlockData(blockPos12.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos12.add(0, 0, 1)).getBlock())) {
            return new BlockData(blockPos12.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos12.add(0, 0, -1)).getBlock())) {
            return new BlockData(blockPos12.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos blockPos13 = blockPos10.add(0, 0, 1);
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos13.add(0, -1, 0)).getBlock())) {
            return new BlockData(blockPos13.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos13.add(-1, 0, 0)).getBlock())) {
            return new BlockData(blockPos13.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos13.add(0, 1, 0)).getBlock())) {
            return new BlockData(blockPos13.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos13.add(1, 0, 0)).getBlock())) {
            return new BlockData(blockPos13.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos13.add(0, 0, 1)).getBlock())) {
            return new BlockData(blockPos13.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos13.add(0, 0, -1)).getBlock())) {
            return new BlockData(blockPos13.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos blockPos14 = blockPos10.add(0, 0, -1);
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos14.add(0, -1, 0)).getBlock())) {
            return new BlockData(blockPos14.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos14.add(0, 1, 0)).getBlock())) {
            return new BlockData(blockPos14.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos14.add(-1, 0, 0)).getBlock())) {
            return new BlockData(blockPos14.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos14.add(1, 0, 0)).getBlock())) {
            return new BlockData(blockPos14.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos14.add(0, 0, 1)).getBlock())) {
            return new BlockData(blockPos14.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(blockPos14.add(0, 0, -1)).getBlock())) {
            return new BlockData(blockPos14.add(0, 0, -1), EnumFacing.SOUTH);
        }
        return null;
    }

    public void place(BlockPos blockPos, EnumFacing enumFacing) {
        int n;
        BlockPos blockPos2 = blockPos;
        if (enumFacing == EnumFacing.UP) {
            blockPos2 = blockPos2.add(0, -1, 0);
        } else if (enumFacing == EnumFacing.NORTH) {
            blockPos2 = blockPos2.add(0, 0, 1);
        } else if (enumFacing == EnumFacing.SOUTH) {
            blockPos2 = blockPos2.add(0, 0, -1);
        } else if (enumFacing == EnumFacing.EAST) {
            blockPos2 = blockPos2.add(-1, 0, 0);
        } else if (enumFacing == EnumFacing.WEST) {
            blockPos2 = blockPos2.add(1, 0, 0);
        }
        int n2 = Scaffold.mc.player.inventory.currentItem;
        int n3 = -1;
        for (n = 0; n < 9; ++n) {
            ItemStack object = Scaffold.mc.player.inventory.getStackInSlot(n);
            if (InventoryUtil.isNull(object) || !(object.getItem() instanceof ItemBlock) || !Block.getBlockFromItem((Item)object.getItem()).getDefaultState().isFullBlock()) continue;
            n3 = n;
            break;
        }
        if (n3 == -1) {
            return;
        }
        n = 0;
        if (!Scaffold.mc.player.isSneaking() && BlockUtil.blackList.contains(Scaffold.mc.world.getBlockState(blockPos2).getBlock())) {
            Scaffold.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Scaffold.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            n = 1;
        }
        if (!(Scaffold.mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock)) {
            Scaffold.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(n3));
            Scaffold.mc.player.inventory.currentItem = n3;
            Scaffold.mc.playerController.updateController();
        }
        if (Scaffold.mc.gameSettings.keyBindJump.isKeyDown()) {
            Scaffold.mc.player.motionX *= 0.3;
            Scaffold.mc.player.motionZ *= 0.3;
            Scaffold.mc.player.jump();
            if (this.timer.passedMs(1500L)) {
                Scaffold.mc.player.motionY = -0.28;
                this.timer.reset();
            }
        }
        if (this.rotation.getValue().booleanValue()) {
            float[] angle = MathUtil.calcAngle(Scaffold.mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((double)((float)this.pos.getX() + 0.5f), (double)((float)this.pos.getY() - 0.5f), (double)((float)this.pos.getZ() + 0.5f)));
            Scaffold.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(angle[0], (float)MathHelper.normalizeAngle((int)((int)angle[1]), (int)360), Scaffold.mc.player.onGround));
        }
        Scaffold.mc.playerController.processRightClickBlock(Scaffold.mc.player, Scaffold.mc.world, blockPos2, enumFacing, new Vec3d(0.5, 0.5, 0.5), EnumHand.MAIN_HAND);
        Scaffold.mc.player.swingArm(EnumHand.MAIN_HAND);
        Scaffold.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(n2));
        Scaffold.mc.player.inventory.currentItem = n2;
        Scaffold.mc.playerController.updateController();
        if (n != 0) {
            Scaffold.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Scaffold.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }
    }

    private static class BlockData {
        public BlockPos position;
        public EnumFacing face;

        public BlockData(BlockPos blockPos, EnumFacing enumFacing) {
            this.position = blockPos;
            this.face = enumFacing;
        }
    }

    public static enum Mode {
        New,
        Old;

    }
}