package io.wispforest.uwu.nodelang;

import java.util.List;

public record NodeScript(List<AllowChatNode> allowChatNodes, List<HudNode> hudNodes, List<TickActionNode> tickNodes) {

    public NodeScript(List<AllowChatNode> allowChatNodes, List<HudNode> hudNodes, List<TickActionNode> tickNodes) {
        this.allowChatNodes = allowChatNodes == null ? List.of() : List.copyOf(allowChatNodes);
        this.hudNodes = hudNodes == null ? List.of() : List.copyOf(hudNodes);
        this.tickNodes = tickNodes == null ? List.of() : List.copyOf(tickNodes);
    }

    public static NodeScript empty() {
        return new NodeScript(List.of(), List.of(), List.of());
    }
}
