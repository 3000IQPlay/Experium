package dev._3000IQPlay.experium.event.events;

import dev._3000IQPlay.experium.event.EventStage;
import net.minecraft.util.MovementInput;

public class PlayerUpdateMoveEvent
        extends EventStage {
    public MovementInput movementInput;

    public PlayerUpdateMoveEvent(MovementInput movementInput) {
        this.movementInput = movementInput;
    }
}