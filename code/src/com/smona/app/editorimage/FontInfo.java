package com.smona.app.editorimage;

public class FontInfo {
    public int fontId;
    public int fontSize;
    public float rotation = 0;
    public String coord;
    public String content;
    public String color;
    public String align;
    public int line = 1;
    public String name;
    public boolean isSelected = false;

    public String toString() {
        return "fontId: " + fontId + ", fontSize: " + fontSize + ", rotation: "
                + rotation + ", coord: " + coord + ", conetnt: " + content
                + ", color: " + color + ", line: " + line + ", name: " + name
                + ", align: " + align + ", isBatchState: " + isSelected;
    }
}
