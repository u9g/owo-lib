package io.wispforest.owo.nodescript.nodes;

import io.wispforest.owo.nodescript.model.Node;
import io.wispforest.owo.nodescript.model.PortType;

import java.util.Map;

/**
 * Event node for client tick events.
 */
public class ClientTickEventNode extends Node {

    public static final String TYPE = "event:client_tick";

    public ClientTickEventNode() {
        super("Client Tick Event");
    }

    public ClientTickEventNode(String id, double x, double y) {
        super(id, "Client Tick Event", x, y);
    }

    @Override
    protected void initPorts() {
        addOutputPort("exec", "Execute", PortType.EXECUTION);
    }

    @Override
    public String getNodeType() {
        return TYPE;
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> inputs) {
        return Map.of("exec", true);
    }
}
