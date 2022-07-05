package dev._3000IQPlay.experium.event.events;

import dev._3000IQPlay.experium.event.EventStage;
import dev._3000IQPlay.experium.features.setting.Setting;

public class ValueChangeEvent
        extends EventStage {
    public Setting setting;
    public Object value;

    public ValueChangeEvent(Setting setting, Object value) {
        this.setting = setting;
        this.value = value;
    }
}

