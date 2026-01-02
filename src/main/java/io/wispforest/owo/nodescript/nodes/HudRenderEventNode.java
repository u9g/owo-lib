package io.wispforest.owo.nodescript.nodes;

import io.wispforest.owo.nodescript.model.Node;
import io.wispforest.owo.nodescript.model.PortType;

import java.util.Map;

/**
 * Event node for Fabric's HudRenderCallback.
 * Triggers when HUD is rendered.
 */
public class HudRenderEventNode extends Node {

    public static final String TYPE = "event:hud_render";

    public HudRenderEventNode() {
        super("HUD Render Event");
    }

    public HudRenderEventNode(String id, double x, double y) {
        super(id, "HUD Render Event", x, y);
    }

    @Override
    protected void initPorts() {
        addOutputPort("tick_delta", "Tick Delta", PortType.NUMBER);
        addOutputPort("exec", "Execute", PortType.EXECUTION);
    }

    @Override
    public String getNodeType() {
        return TYPE;
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> inputs) {
        float tickDelta = ((Number) inputs.getOrDefault("tick_delta", 0f)).floatValue();
        return Map.of(
            "tick_delta", tickDelta,
            "exec", true
        );
    }
}
