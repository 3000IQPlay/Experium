package dev._3000IQPlay.experium.event.events;

import dev._3000IQPlay.experium.event.EventStage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class RightClickItemEvent
        extends EventStage {
    private final EntityPlayer player;
    private final World worldIn;
    private final EnumHand hand;

    public RightClickItemEvent(EntityPlayer player, World worldIn, EnumHand hand) {
        this.player = player;
        this.worldIn = worldIn;
        this.hand = hand;
    }

    public EntityPlayer getPlayer() {
        return this.player;
    }

    public World getWorldIn() {
        return this.worldIn;
    }

    public EnumHand getHand() {
        return this.hand;
    }
}

