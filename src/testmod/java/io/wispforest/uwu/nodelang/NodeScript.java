package io.wispforest.uwu.nodelang;

import java.util.List;

public record NodeScript(List<AllowChatNode> allowChatNodes, List<HudNode> hudNodes, List<TickActionNode> tickNodes) {

    public static NodeScript empty() {
        return new NodeScript(List.of(), List.of(), List.of());
    }
}
