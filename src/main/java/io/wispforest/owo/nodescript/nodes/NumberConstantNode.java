package io.wispforest.owo.nodescript.nodes;

import io.wispforest.owo.nodescript.model.Node;
import io.wispforest.owo.nodescript.model.PortType;

import java.util.Map;

/**
 * Node that provides a constant number value.
 */
public class NumberConstantNode extends Node {

    public static final String TYPE = "constant:number";

    private double value = 0;

    public NumberConstantNode() {
        super("Number Constant");
    }

    public NumberConstantNode(String id, double x, double y) {
        super(id, "Number Constant", x, y);
    }

    @Override
    protected void initPorts() {
        addOutputPort("value", "Value", PortType.NUMBER);
    }

    @Override
    public String getNodeType() {
        return TYPE;
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> inputs) {
        return Map.of("value", this.value);
    }

    public double value() {
        return this.value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
