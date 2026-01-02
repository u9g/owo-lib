package io.wispforest.uwu.nodelang;

public record TickActionNode(String id, int interval, String actionText) {

    public TickActionNode {
        if (id == null || id.isBlank()) id = "tick";
        if (interval <= 0) interval = 20;
        if (actionText == null) actionText = "";
    }
}
