package com.smona.app.editorimage;

public class FontInfo {
    /*
     * <?xml version="1.0" encoding="UTF-8"?> <font-family> <font
     * content="world 12" size="12" coordinate="200,33" color="#00ff44"
     * line="1"/> <font content="world 13" size="13" coordinate="400,66"
     * color="#00ff44" line="1"/> </font-family>
     */
    public int fontId;
    public int fontSize;
    public float rotation = 0;
    public String coord;
    public String content;
    public String color;
    public int line = 1;
    public String name;

    public String toString() {
        return "fontId: " + fontId + ", fontSize: " + fontSize + ", rotation: "
                + rotation + ", coord: " + coord + ", conetnt: " + content
                + ", color: " + color + ", line: " + line + ", name: " + name;
    }
}
