package com.smona.app.editorimage;

import android.widget.FrameLayout.LayoutParams;

public class LastOpInfo {
    private int[] pos = new int[2];
    private String color;
    private int fontSize;
    private String fontStyle;
    private FontEditorLayer focusView;

    enum State {
        NONE, POS, COLOR, FONTSIZE, FONTSTYLE
    }

    private State mState = State.NONE;

    private void setState(State state) {
        mState = state;
    }

    public void saveLastPos(int left, int top, FontEditorLayer opView) {
        setState(State.POS);
        pos[0] = left;
        pos[1] = top;
        focusView = opView;
    }

    private void resetLastPos() {
        if (mState != State.POS) {
            return;
        }
        LayoutParams params = (LayoutParams) focusView.getLayoutParams();
        params.leftMargin = pos[0];
        params.topMargin = pos[1];
        focusView.requestLayout();
    }

    public void saveLastColor(String color, FontEditorLayer opView) {
        setState(State.COLOR);
        this.color = color;
        focusView = opView;
    }

    private void resetLastColor() {
        if (mState != State.COLOR) {
            return;
        }
    }

    public void saveLastFontSize(int fontSize, FontEditorLayer opView) {
        setState(State.FONTSIZE);
        this.fontSize = fontSize;
        focusView = opView;
    }

    private void resetLastFontSize() {
        if (mState != State.FONTSIZE) {
            return;
        }
        focusView.setFontSize(fontSize);
        FontInfo info = (FontInfo) focusView.getTag();
        info.fontSize = fontSize;
    }

    public void saveLastFontStyle(String fontStyle, FontEditorLayer opView) {
        setState(State.FONTSTYLE);
        this.fontStyle = fontStyle;
        focusView = opView;
    }

    private void resetLastStyle() {
        if (mState != State.FONTSTYLE) {
            return;
        }
    }

    public void resetFeature() {
        WallpaperLog.d("LastOpInfo", "resetFeature mState=" + mState);
        resetLastPos();
        resetLastColor();
        resetLastFontSize();
        resetLastStyle();
        resetAll();
    }

    public void resetAll() {
        mState = State.NONE;
        pos[0] = 0;
        pos[1] = 0;
        color = "";
        fontSize = 0;
        fontStyle = "";
    }
}
