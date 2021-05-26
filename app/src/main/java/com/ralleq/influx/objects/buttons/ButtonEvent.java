package com.ralleq.influx.objects.buttons;

import androidx.annotation.NonNull;

public class ButtonEvent {

    @NonNull
    @Override
    public String toString() {
        return "[ButtonEvent] action: " + action;
    }

    private int action;
    public static final int
            ACTION_PRESS = 0,
            ACTION_HOLD = 1;

    public ButtonEvent(int action) {
        this.action = action;
    }

    public int getAction() {
        return action;
    }

}
