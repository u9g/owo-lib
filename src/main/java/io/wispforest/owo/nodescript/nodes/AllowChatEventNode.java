package io.wispforest.owo.nodescript.nodes;

import io.wispforest.owo.nodescript.model.Node;
import io.wispforest.owo.nodescript.model.PortType;

import java.util.Map;

/**
 * Event node for Fabric's AllowChatEvent.
 * Provides the chat message string and expects a boolean result.
 */
public class AllowChatEventNode extends Node {

    public static final String TYPE = "event:allow_chat";

    public AllowChatEventNode() {
        super("Allow Chat Event");
    }

    public AllowChatEventNode(String id, double x, double y) {
        super(id, "Allow Chat Event", x, y);
    }

    @Override
    protected void initPorts() {
        addOutputPort("message", "Message", PortType.STRING);
        addOutputPort("exec", "Execute", PortType.EXECUTION);
        addInputPort("result", "Result", PortType.BOOLEAN);
    }

    @Override
    public String getNodeType() {
        return TYPE;
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> inputs) {
        // This node is the entry point - inputs contain the event data
        String message = inputs.getOrDefault("message", "").toString();
        return Map.of(
            "message", message,
            "exec", true
        );
    }

    /**
     * Get the result from the connected result port.
     */
    public boolean getResult() {
        var resultPort = getInputPort("result");
        if (resultPort == null || !resultPort.isConnected()) {
            return true; // Default: allow chat
        }

        var connection = resultPort.connection();
        if (connection == null) {
            return true;
        }

        // The result should be set by the execution of connected nodes
        return true;
    }
}
