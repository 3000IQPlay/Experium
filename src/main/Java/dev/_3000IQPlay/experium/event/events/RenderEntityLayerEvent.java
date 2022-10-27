package dev._3000IQPlay.experium.event.events;

import dev._3000IQPlay.experium.event.EventStage;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;

public class RenderEntityLayerEvent
        extends EventStage {
    public EntityLivingBase entity;
    public LayerRenderer<?> layer;

    public RenderEntityLayerEvent(EntityLivingBase entity, LayerRenderer<?> layer) {
        this.entity = entity;
        this.layer = layer;
    }

    public EntityLivingBase getEntity() {
        return this.entity;
    }

    public void setEntity(EntityLivingBase entityLivingBase) {
        this.entity = entityLivingBase;
    }

    public LayerRenderer<?> getLayer() {
        return this.layer;
    }

    public void setLayer(LayerRenderer<?> layerRenderer) {
        this.layer = layerRenderer;
    }

    public EntityLivingBase component1() {
        return this.entity;
    }

    public LayerRenderer<?> component2() {
        return this.layer;
    }

    public RenderEntityLayerEvent copy(EntityLivingBase entity, LayerRenderer<?> layer) {
        return new RenderEntityLayerEvent(entity, layer);
    }

    public static RenderEntityLayerEvent copy$default(RenderEntityLayerEvent renderEntityLayerEvent, EntityLivingBase entityLivingBase, LayerRenderer layerRenderer, int n, Object object) {
        if ((n & 1) != 0) {
            entityLivingBase = renderEntityLayerEvent.entity;
        }
        if ((n & 2) != 0) {
            layerRenderer = renderEntityLayerEvent.layer;
        }
        return renderEntityLayerEvent.copy(entityLivingBase, layerRenderer);
    }

    public String toString() {
        return "RenderEntityLayerEvent(entity=" + this.entity + ", layer=" + this.layer + ')';
    }

    public int hashCode() {
        int result2 = this.entity.hashCode();
        result2 = result2 * 31 + this.layer.hashCode();
        return result2;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RenderEntityLayerEvent)) {
            return false;
        }
        RenderEntityLayerEvent renderEntityLayerEvent = (RenderEntityLayerEvent)other;
        return this.entity == renderEntityLayerEvent.entity && this.layer == renderEntityLayerEvent.layer;
    }
}
