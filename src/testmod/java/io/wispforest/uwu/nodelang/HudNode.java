package io.wispforest.uwu.nodelang;

public record HudNode(String id, String text, int x, int y, int color) {

    public HudNode {
        if (id == null || id.isBlank()) id = "hud";
        if (text == null) text = "";
    }
}
