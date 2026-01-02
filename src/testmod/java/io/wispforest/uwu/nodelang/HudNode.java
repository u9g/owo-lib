package io.wispforest.uwu.nodelang;

public record HudNode(String id, String text, int x, int y, int color) {

    public HudNode(String id, String text, int x, int y, int color) {
        this.id = id == null || id.isBlank() ? "hud" : id;
        this.text = text == null ? "" : text;
        this.x = x;
        this.y = y;
        this.color = color;
    }
}
