package dev._3000IQPlay.experium.event.events;

import dev._3000IQPlay.experium.event.EventStage;

public class PerspectiveEvent
        extends EventStage {
    private float aspect;

    public PerspectiveEvent(float f) {
        this.aspect = f;
    }

    public float getAspect() {
        return this.aspect;
    }

    public void setAspect(float f) {
        this.aspect = f;
    }
}
