package dev._3000IQPlay.experium.features.modules.player;

import dev._3000IQPlay.experium.event.events.Render3DEvent;
import dev._3000IQPlay.experium.event.events.UpdateWalkingPlayerEvent;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.util.*;
import dev._3000IQPlay.experium.util.Timer;
import net.minecraft.block.BlockObsidian;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AutoBuilder
        extends Module {
    private final Setting < Settings > settings = this.register ( new Setting <> ( "Settings" , Settings.PATTERN ) );
    // PATTERN
    private final Setting < Mode > mode = this.register ( new Setting <> ( "Mode" , Mode.STAIRS , v -> this.settings.getValue ( ) == Settings.PATTERN ) );
    private final Setting < Direction > stairDirection = this.register ( new Setting <> ( "Direction" , Direction.NORTH , v -> this.mode.getValue ( ) == Mode.STAIRS && this.settings.getValue ( ) == Settings.PATTERN ) );
    private final Setting < Integer > width = this.register ( new Setting <> ( "StairWidth" , 40 , 1 , 100 , v -> this.mode.getValue ( ) == Mode.STAIRS && this.settings.getValue ( ) == Settings.PATTERN ) );
    private final Setting < Boolean > setPos = this.register ( new Setting <> ( "ResetPos" , false , v -> this.settings.getValue ( ) == Settings.PATTERN && ( this.mode.getValue ( ) == Mode.STAIRS || ( this.mode.getValue ( ) == Mode.FLAT && ! this.dynamic.getValue ( ) ) ) ) );
    private final Setting < Boolean > dynamic = this.register ( new Setting <> ( "Dynamic" , true , v -> this.mode.getValue ( ) == Mode.FLAT && this.settings.getValue ( ) == Settings.PATTERN ) );
    // PLACE
    private final Setting < Float > range = this.register ( new Setting <> ( "Range" , 4.0f , 1.0f , 6.0f , v -> this.settings.getValue ( ) == Settings.PLACE ) );
    private final Setting < Integer > blocksPerTick = this.register ( new Setting <> ( "Blocks/Tick" , 3 , 1 , 8 , v -> this.settings.getValue ( ) == Settings.PLACE ) );
    private final Setting < Integer > placeDelay = this.register ( new Setting <> ( "PlaceDelay" , 150 , 0 , 500 , v -> this.settings.getValue ( ) == Settings.PLACE ) );
    private final Setting < Boolean > rotate = this.register ( new Setting <> ( "Rotate" , true , v -> this.settings.getValue ( ) == Settings.PLACE ) );
    private final Setting < Boolean > altRotate = this.register ( new Setting <> ( "AltRotate" , false , v -> this.rotate.getValue ( ) && this.settings.getValue ( ) == Settings.PLACE ) );
    private final Setting < Boolean > ground = this.register ( new Setting <> ( "NoJump" , true , v -> this.settings.getValue ( ) == Settings.PLACE ) );
    private final Setting < Boolean > noMove = this.register ( new Setting <> ( "NoMove" , true , v -> this.settings.getValue ( ) == Settings.PLACE ) );
    private final Setting < Boolean > packet = this.register ( new Setting <> ( "Packet" , true , v -> this.settings.getValue ( ) == Settings.PLACE ) );
    // RENDER
    private final Setting < Boolean > render = this.register ( new Setting <> ( "Render" , true , v -> this.settings.getValue ( ) == Settings.RENDER ) );
    private final Setting < Boolean > colorSync = this.register ( new Setting < > ( "CSync" , false , v -> this.settings.getValue ( ) == Settings.RENDER && this.render.getValue ( ) ) );
    private final Setting < Boolean > box = this.register ( new Setting <> ( "Box" , true , v -> this.settings.getValue ( ) == Settings.RENDER && this.render.getValue ( ) ) );
	private final Setting<Color> bC = register(new Setting("BoxColor", new Color(40, 192, 255, 40), v -> this.settings.getValue ( ) == Settings.RENDER && this.render.getValue() && this.box.getValue()));
    private final Setting < Boolean > outline = this.register ( new Setting <> ( "Outline" , true , v -> this.settings.getValue ( ) == Settings.RENDER && this.render.getValue ( ) ) );
	private final Setting<Color> oC = register(new Setting("OutlineColor", new Color(40, 192, 255, 255), v -> this.settings.getValue ( ) == Settings.RENDER && this.render.getValue() && this.outline.getValue()));
    private final Setting < Float > lineWidth = this.register ( new Setting <> ( "LineWidth" , 1.5f , 0.1f , 5.0f , v -> this.settings.getValue ( ) == Settings.RENDER && this.render.getValue() && this.outline.getValue()));
    // MISC
    private final Setting < Boolean > keepPos = this.register ( new Setting <> ( "KeepOldPos" , false , v -> this.settings.getValue ( ) == Settings.MISC ) );
    private final Setting < Updates > updates = this.register ( new Setting <> ( "Update" , Updates.TICK , v -> this.settings.getValue ( ) == Settings.MISC ) );
    private final Setting < Switch > switchMode = this.register ( new Setting <> ( "Switch" , Switch.SILENT , v -> this.settings.getValue ( ) == Settings.MISC ) );
    private final Setting < Boolean > allBlocks = this.register ( new Setting <> ( "AllBlocks" , true , v -> this.settings.getValue ( ) == Settings.MISC ) );
    private BlockPos startPos;
    private final Timer timer = new Timer ();
    private final List < BlockPos > placepositions = new ArrayList <> (  );
    private int blocksThisTick;
    private int lastSlot;
    private int blockSlot;

    public AutoBuilder ( ) {
        super ( "AutoBuilder" , "Auto Builds" , Module.Category.PLAYER , true , false , false );
    }

    @Override
    public void onTick ( ) {
        if ( this.updates.getValue ( ) == Updates.TICK )
            this.doAutoBuilder ( );
    }

    @Override
    public void onUpdate ( ) {
        if ( this.updates.getValue ( ) == Updates.UPDATE )
            this.doAutoBuilder ( );
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer ( UpdateWalkingPlayerEvent event ) {
        if ( this.updates.getValue ( ) == Updates.WALKING && event.getStage ( ) != 1 )
            this.doAutoBuilder ( );
    } // not gonna do rotate thing here cuz i don't really care and this module works well enough

    @Override
    public void onRender3D ( Render3DEvent event ) {
        if ( this.placepositions == null  || ! this.render.getValue ( ) ) return;
        Color outline = new Color (this.oC.getValue().getRed(), this.oC.getValue().getGreen(), this.oC.getValue().getBlue(), this.oC.getValue().getAlpha());
        Color box = new Color (this.bC.getValue().getRed(), this.bC.getValue().getGreen(), this.bC.getValue().getBlue(), this.bC.getValue().getAlpha());
        this.placepositions.forEach ( pos -> {
            RenderUtil.drawSexyBox (
                    new AxisAlignedBB ( pos ) ,
                    box ,
                    outline ,
                    this.lineWidth.getValue ( ) ,
                    this.outline.getValue ( ) ,
                    this.box.getValue ( ) ,
                    this.colorSync.getValue ( ) ,
                    1.0f ,
                    1.0f ,
                    1.0f
            );
        } );
    }

    @Override
    public void onEnable ( ) {
        this.placepositions.clear ( );
        if ( ! this.keepPos.getValue ( ) || this.startPos == null )
            this.startPos = new BlockPos ( AutoBuilder.mc.player.posX , Math.ceil ( AutoBuilder.mc.player.posY ) , AutoBuilder.mc.player.posZ ).down ( );
        this.blocksThisTick = 0;
        this.lastSlot = AutoBuilder.mc.player.inventory.currentItem;
        this.timer.reset ( );
    }

    private void doAutoBuilder ( ) {
        if ( ! this.check ( ) ) return;
        for ( BlockPos pos : this.placepositions ) {
            if ( this.blocksThisTick >= this.blocksPerTick.getValue ( ) ) {
                this.doSwitch ( true );
                return;
            }
            int canPlace = BlockUtil.isPositionPlaceable ( pos , false , true );
            if ( canPlace == 3 ) {
                BlockUtil.placeBlockNotRetarded ( pos , EnumHand.MAIN_HAND , this.rotate.getValue ( ) , this.packet.getValue ( ) , this.altRotate.getValue ( ) );
                this.blocksThisTick++;
            } else if ( canPlace == 2 && this.mode.getValue ( ) == Mode.STAIRS )
                if ( BlockUtil.isPositionPlaceable ( pos.down ( ) , false , true ) == 3 ) {
                    BlockUtil.placeBlockNotRetarded ( pos.down ( ) , EnumHand.MAIN_HAND , this.rotate.getValue ( ) , this.packet.getValue ( ) , this.altRotate.getValue ( ) );
                    this.blocksThisTick++;
                } else {
                    switch ( this.stairDirection.getValue ( ) ) {
                        case SOUTH:
                            if ( BlockUtil.isPositionPlaceable ( pos.south ( ) , false , true ) == 3 ) {
                                BlockUtil.placeBlockNotRetarded ( pos.south ( ) , EnumHand.MAIN_HAND , this.rotate.getValue ( ) , this.packet.getValue ( ) , this.altRotate.getValue ( ) );
                                this.blocksThisTick++;
                            }
                            break;
                        case WEST:
                            if ( BlockUtil.isPositionPlaceable ( pos.west ( ) , false , true ) == 3 ) {
                                BlockUtil.placeBlockNotRetarded ( pos.west ( ) , EnumHand.MAIN_HAND , this.rotate.getValue ( ) , this.packet.getValue ( ) , this.altRotate.getValue ( ) );
                                this.blocksThisTick++;
                            }
                            break;
                        case NORTH:
                            if ( BlockUtil.isPositionPlaceable ( pos.north ( ) , false , true ) == 3 ) {
                                BlockUtil.placeBlockNotRetarded ( pos.north ( ) , EnumHand.MAIN_HAND , this.rotate.getValue ( ) , this.packet.getValue ( ) , this.altRotate.getValue ( ) );
                                this.blocksThisTick++;
                            }
                            break;
                        case EAST:
                            if ( BlockUtil.isPositionPlaceable ( pos.east ( ) , false , true ) == 3 ) {
                                BlockUtil.placeBlockNotRetarded ( pos.east ( ) , EnumHand.MAIN_HAND , this.rotate.getValue ( ) , this.packet.getValue ( ) , this.altRotate.getValue ( ) );
                                this.blocksThisTick++;
                            }
                    }
                }
        }
        this.doSwitch ( true );
    }

    private boolean doSwitch ( boolean back ) {
        Item i = AutoBuilder.mc.player.getHeldItemMainhand ( ).getItem ( );
        switch ( this.switchMode.getValue ( ) ) {
            case NONE:
                if ( i instanceof ItemBlock ) {
                    if ( this.allBlocks.getValue ( ) ) return true;
                    else return ( (ItemBlock) i ).getBlock ( ) instanceof BlockObsidian;
                }
                return false;
            case NORMAL:
                if ( ! back )
                    InventoryUtil.switchToHotbarSlot ( this.blockSlot , false );
                break;
            case SILENT:
                if ( i instanceof ItemBlock ) {
                    if ( this.allBlocks.getValue ( ) ) break;
                    else if ( ( (ItemBlock) i ).getBlock ( ) instanceof BlockObsidian ) break;
                } // checks if the player is already holding the right block before switching
                if ( lastSlot == - 1 ) break;
                if ( back ) AutoBuilder.mc.player.connection.sendPacket ( new CPacketHeldItemChange ( this.lastSlot ) );
                else AutoBuilder.mc.player.connection.sendPacket ( new CPacketHeldItemChange ( this.blockSlot ) );
        }
        return true;
    }

    private boolean check ( ) {
        if ( this.setPos.getValue ( ) ) { // has to be the first thing
            this.startPos = new BlockPos ( AutoBuilder.mc.player.posX , Math.ceil ( AutoBuilder.mc.player.posY ) , AutoBuilder.mc.player.posZ ).down ( );
            this.setPos.setValue ( false );
        }
        this.getPositions ( );
        if ( this.placepositions.isEmpty ( ) ) return false;
        if ( ! this.timer.passedMs ( this.placeDelay.getValue ( ) ) ) return false;
        this.timer.reset ( );
        this.blocksThisTick = 0;
        this.lastSlot = AutoBuilder.mc.player.inventory.currentItem;
        this.blockSlot = this.allBlocks.getValue ( ) ? InventoryUtil.findAnyBlock ( ) : InventoryUtil.findHotbarBlock ( BlockObsidian.class );
        if ( this.ground.getValue ( ) && ! AutoBuilder.mc.player.onGround ) return false;
        if ( this.blockSlot == - 1  ) return false;
        if ( noMove.getValue ( ) && ( AutoBuilder.mc.player.moveForward != 0 || AutoBuilder.mc.player.moveStrafing != 0 ) ) return false;
        return this.doSwitch ( false );
    }

    private void getPositions ( ) {
        if ( startPos == null ) return;
        placepositions.clear ( );
        for ( BlockPos pos : BlockUtil.getSphere ( new BlockPos ( AutoBuilder.mc.player.posX , Math.ceil ( AutoBuilder.mc.player.posY ) , AutoBuilder.mc.player.posZ ).up ( ) , this.range.getValue ( ) , this.range.getValue ( ).intValue ( ) , false , true , 0  ) ) {
            if ( placepositions.contains ( pos ) ) continue;
            if ( ! AutoBuilder.mc.world.isAirBlock ( pos ) ) continue;
            if ( this.mode.getValue ( ) == Mode.STAIRS ) {
                switch ( this.stairDirection.getValue ( ) ) {
                    case NORTH:
                        if ( ( this.startPos.getZ ( ) - pos.getZ ( ) == pos.getY ( ) - this.startPos.getY ( ) ) && Math.abs ( pos.getX ( ) - this.startPos.getX ( ) ) < this.width.getValue ( ) / 2 ) {
                            this.placepositions.add ( pos );
                        }
                        break;
                    case EAST:
                        if ( ( pos.getX ( ) - this.startPos.getX ( ) == pos.getY ( ) - this.startPos.getY ( ) ) && Math.abs ( pos.getZ ( ) - this.startPos.getZ ( ) ) < this.width.getValue ( ) / 2 ) {
                            this.placepositions.add ( pos );
                        }
                        break;
                    case SOUTH:
                        if ( ( pos.getZ ( ) - this.startPos.getZ ( ) == pos.getY ( ) - this.startPos.getY ( ) ) && Math.abs ( this.startPos.getX ( ) - pos.getX ( ) ) < this.width.getValue ( ) / 2 ) {
                            this.placepositions.add ( pos );
                        }
                        break;
                    case WEST:
                        if ( ( this.startPos.getX ( ) - pos.getX ( ) == pos.getY ( ) - this.startPos.getY ( ) ) && Math.abs ( this.startPos.getZ ( ) - pos.getZ ( ) ) < this.width.getValue ( ) / 2 ) {
                            this.placepositions.add ( pos );
                        }
                        break;
                }
            } else if ( this.mode.getValue ( ) == Mode.FLAT ) {
                if ( pos.getY ( ) != ( this.dynamic.getValue ( ) ? Math.ceil ( AutoBuilder.mc.player.posY ) - 1 : this.startPos.getY ( ) ) ) continue;
                this.placepositions.add ( pos );
            }
        }
    }

    public enum Mode {
        STAIRS,
        FLAT
		
    }

    public enum Switch {
        NONE,
        NORMAL,
        SILENT
		
    }

    public enum Updates {
        TICK,
        UPDATE,
        WALKING
		
    }

    public enum Direction {
        WEST,
        SOUTH,
        EAST,
        NORTH
		
    }

    public enum Settings {
        MISC,
        PATTERN,
        PLACE,
        RENDER
		
    }
}
