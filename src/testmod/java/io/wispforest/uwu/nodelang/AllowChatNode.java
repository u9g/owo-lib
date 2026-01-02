package io.wispforest.uwu.nodelang;

public record AllowChatNode(String id, String includes, boolean allow) {

    public AllowChatNode(String id, String includes, boolean allow) {
        this.id = id == null || id.isBlank() ? "allow-chat" : id;
        this.includes = includes == null ? "" : includes;
        this.allow = allow;
    }
}
