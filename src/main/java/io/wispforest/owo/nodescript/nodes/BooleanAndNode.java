package io.wispforest.owo.nodescript.nodes;

import io.wispforest.owo.nodescript.model.Node;
import io.wispforest.owo.nodescript.model.PortType;

import java.util.Map;

/**
 * Node that performs boolean AND operation.
 */
public class BooleanAndNode extends Node {

    public static final String TYPE = "logic:boolean_and";

    public BooleanAndNode() {
        super("AND");
    }

    public BooleanAndNode(String id, double x, double y) {
        super(id, "AND", x, y);
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
        return Map.of("result", a && b);
    }
}
