package io.wispforest.uwu.nodelang;

public record TickActionNode(String id, int interval, String actionText) {

    public TickActionNode(String id, int interval, String actionText) {
        this.id = id == null || id.isBlank() ? "tick" : id;
        this.interval = interval <= 0 ? 20 : interval;
        this.actionText = actionText == null ? "" : actionText;
    }
}
