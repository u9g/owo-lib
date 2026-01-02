package io.wispforest.owo.nodescript.nodes;

import io.wispforest.owo.nodescript.model.Node;
import io.wispforest.owo.nodescript.model.PortType;

import java.util.Map;

/**
 * Node that performs boolean OR operation.
 */
public class BooleanOrNode extends Node {

    public static final String TYPE = "logic:boolean_or";

    public BooleanOrNode() {
        super("OR");
    }

    public BooleanOrNode(String id, double x, double y) {
        super(id, "OR", x, y);
    }

    @Override
    protected void initPorts() {
        addInputPort("a", "A", PortType.BOOLEAN);
        addInputPort("b", "B", PortType.BOOLEAN);
        addOutputPort("result", "Result", PortType.BOOLEAN);
    }

    @Override
    public String getNodeType() {
        return TYPE;
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> inputs) {
        boolean a = (Boolean) inputs.getOrDefault("a", false);
        boolean b = (Boolean) inputs.getOrDefault("b", false);
        return Map.of("result", a || b);
    }
}
