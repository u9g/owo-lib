package io.wispforest.owo.nodescript.nodes;

import io.wispforest.owo.nodescript.model.Node;
import io.wispforest.owo.nodescript.model.PortType;

import java.util.Map;

/**
 * Node that compares two numbers.
 */
public class NumberCompareNode extends Node {

    public static final String TYPE = "logic:number_compare";

    public NumberCompareNode() {
        super("Number Compare");
    }

    public NumberCompareNode(String id, double x, double y) {
        super(id, "Number Compare", x, y);
    }

    @Override
    protected void initPorts() {
        addInputPort("a", "A", PortType.NUMBER);
        addInputPort("b", "B", PortType.NUMBER);
        addOutputPort("equals", "A == B", PortType.BOOLEAN);
        addOutputPort("less", "A < B", PortType.BOOLEAN);
        addOutputPort("greater", "A > B", PortType.BOOLEAN);
    }

    @Override
    public String getNodeType() {
        return TYPE;
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> inputs) {
        double a = ((Number) inputs.getOrDefault("a", 0)).doubleValue();
        double b = ((Number) inputs.getOrDefault("b", 0)).doubleValue();
        return Map.of(
            "equals", a == b,
            "less", a < b,
            "greater", a > b
        );
    }
}
