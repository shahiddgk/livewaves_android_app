package com.app.livewave.models.ParameterModels;

public class OnTouch {
    boolean isClick;

    public OnTouch(boolean isClick) {
        this.isClick = isClick;
    }

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }
}
