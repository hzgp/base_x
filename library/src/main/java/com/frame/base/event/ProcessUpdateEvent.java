package com.frame.base.event;

/**
 * Desc:
 * Author:Zhu
 * Date:2022/8/4
 */
public class ProcessUpdateEvent {
    private boolean isBackground;

    public ProcessUpdateEvent(boolean isBackground) {
        this.isBackground = isBackground;
    }

    public boolean isBackground() {
        return isBackground;
    }
}