package io.wispforest.owo.nodescript.nodes;

import io.wispforest.owo.nodescript.model.Node;
import io.wispforest.owo.nodescript.model.PortType;

import java.util.Map;

/**
 * Node that performs a NOT operation on a boolean.
 */
public class BooleanNotNode extends Node {

    public static final String TYPE = "logic:boolean_not";

    public BooleanNotNode() {
        super("NOT");
    }

    public BooleanNotNode(String id, double x, double y) {
        super(id, "NOT", x, y);
    }

    @Override
    protected void initPorts() {
        addInputPort("value", "Value", PortType.BOOLEAN);
        addOutputPort("result", "Result", PortType.BOOLEAN);
    }

    @Override
    public String getNodeType() {
        return TYPE;
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> inputs) {
        boolean value = (Boolean) inputs.getOrDefault("value", false);
        return Map.of("result", !value);
    }
}
