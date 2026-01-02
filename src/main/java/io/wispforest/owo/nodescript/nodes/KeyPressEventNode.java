package io.wispforest.owo.nodescript.nodes;

import io.wispforest.owo.nodescript.model.Node;
import io.wispforest.owo.nodescript.model.PortType;

import java.util.Map;

/**
 * Event node for key press events.
 */
public class KeyPressEventNode extends Node {

    public static final String TYPE = "event:key_press";

    public KeyPressEventNode() {
        super("Key Press Event");
    }

    public KeyPressEventNode(String id, double x, double y) {
        super(id, "Key Press Event", x, y);
    }

    @Override
    protected void initPorts() {
        addOutputPort("key_code", "Key Code", PortType.NUMBER);
        addOutputPort("exec", "Execute", PortType.EXECUTION);
    }

    @Override
    public String getNodeType() {
        return TYPE;
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> inputs) {
        int keyCode = ((Number) inputs.getOrDefault("key_code", 0)).intValue();
        return Map.of(
            "key_code", keyCode,
            "exec", true
        );
    }
}
