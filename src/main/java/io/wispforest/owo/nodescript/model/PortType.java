package io.wispforest.owo.nodescript.model;

/**
 * Defines the data type that can flow through a port.
 */
public enum PortType {
    STRING,
    BOOLEAN,
    NUMBER,
    EXECUTION,
    ANY;

    /**
     * Checks if a connection can be made from this type to the target type.
     */
    public boolean canConnectTo(PortType target) {
        if (this == ANY || target == ANY) {
            return true;
        }
        return this == target;
    }
}
