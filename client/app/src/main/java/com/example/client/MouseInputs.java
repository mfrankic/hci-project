package com.example.client;

import android.content.Context;

public class MouseInputs {

    private float xPosition, yPosition;
    private boolean leftButton, resetButton;
    private Context context;

    public MouseInputs(Context context) {
        this.context = context;
    }

    public void setLeftButton(boolean leftButton) {
        this.leftButton = leftButton;
    }

    public void setResetButton(boolean resetButton) {
        this.resetButton = resetButton;
    }

    public boolean getLeftButton() {
        return leftButton;
    }

    public boolean getResetButton() {
        return resetButton;
    }

    public void changeXPosition(float x) {
        xPosition += x;
    }

    public void changeYPosition(float y) {
        yPosition += y;
    }
}