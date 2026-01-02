package io.wispforest.uwu.nodelang;

public record AllowChatNode(String id, String includes, boolean allow) {

    public AllowChatNode {
        if (id == null || id.isBlank()) id = "allow-chat";
        if (includes == null) includes = "";
    }
}
