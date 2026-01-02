package io.wispforest.owo.nodescript.ui;

import io.wispforest.owo.braid.core.Color;
import io.wispforest.owo.braid.core.Insets;
import io.wispforest.owo.braid.core.cursor.CursorStyle;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.proxy.WidgetState;
import io.wispforest.owo.braid.framework.widget.StatefulWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.basic.*;
import io.wispforest.owo.braid.widgets.button.MessageButton;
import io.wispforest.owo.braid.widgets.flex.Column;
import io.wispforest.owo.braid.widgets.flex.Row;
import io.wispforest.owo.braid.widgets.label.Label;
import io.wispforest.owo.braid.widgets.label.LabelStyle;
import io.wispforest.owo.nodescript.model.Node;
import io.wispforest.owo.nodescript.model.NodePort;
import io.wispforest.owo.nodescript.model.PortType;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Widget representing a single node in the node editor.
 */
public class NodeWidget extends StatefulWidget {

    private static final Color HEADER_COLOR = Color.rgb(0x3A3D41);
    private static final Color BODY_COLOR = Color.rgb(0x2D2D2D);
    private static final Color BORDER_COLOR = Color.rgb(0x5A5A5A);
    private static final Color SELECTED_BORDER_COLOR = Color.rgb(0x4A90D9);

    private static final Color STRING_PORT_COLOR = Color.rgb(0x2ECC71);
    private static final Color BOOLEAN_PORT_COLOR = Color.rgb(0xE74C3C);
    private static final Color NUMBER_PORT_COLOR = Color.rgb(0x3498DB);
    private static final Color EXECUTION_PORT_COLOR = Color.rgb(0xF39C12);
    private static final Color ANY_PORT_COLOR = Color.rgb(0x9B59B6);

    private final Node node;
    private final boolean selected;
    private final Runnable onSelect;
    private final PortClickHandler onPortClick;
    private final NodeDragHandler onDrag;

    public NodeWidget(Node node, boolean selected, Runnable onSelect, PortClickHandler onPortClick, NodeDragHandler onDrag) {
        this.node = node;
        this.selected = selected;
        this.onSelect = onSelect;
        this.onPortClick = onPortClick;
        this.onDrag = onDrag;
    }

    @Override
    public WidgetState<NodeWidget> createState() {
        return new State();
    }

    public interface PortClickHandler {
        void onPortClick(NodePort port, double x, double y);
    }

    public interface NodeDragHandler {
        void onDrag(double dx, double dy);
    }

    private class State extends WidgetState<NodeWidget> {

        @Override
        public Widget build(BuildContext context) {
            var borderColor = selected ? SELECTED_BORDER_COLOR : BORDER_COLOR;

            return new MouseArea(
                widget -> widget
                    .clickCallback((x, y, button, modifiers) -> {
                        onSelect.run();
                        return true;
                    })
                    .dragCallback((x, y, dx, dy) -> onDrag.onDrag(dx, dy))
                    .cursorStyle(CursorStyle.MOVE),
                new Box(
                    borderColor,
                    true,
                    new Padding(
                        Insets.all(2),
                        new Column(
                            // Header
                            new Box(
                                HEADER_COLOR,
                                new Padding(
                                    Insets.of(4, 8, 4, 8),
                                    new Label(
                                        LabelStyle.SHADOW,
                                        true,
                                        Component.literal(node.name())
                                    )
                                )
                            ),
                            // Body with ports
                            new Box(
                                BODY_COLOR,
                                new Padding(
                                    Insets.all(4),
                                    new Row(
                                        // Input ports
                                        new Column(
                                            buildInputPorts()
                                        ),
                                        new Padding(Insets.horizontal(10)),
                                        // Output ports
                                        new Column(
                                            buildOutputPorts()
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            );
        }

        private List<Widget> buildInputPorts() {
            List<Widget> portWidgets = new ArrayList<>();
            for (var port : node.inputPorts()) {
                portWidgets.add(buildPort(port, true));
            }
            if (portWidgets.isEmpty()) {
                portWidgets.add(new Padding(Insets.none()));
            }
            return portWidgets;
        }

        private List<Widget> buildOutputPorts() {
            List<Widget> portWidgets = new ArrayList<>();
            for (var port : node.outputPorts()) {
                portWidgets.add(buildPort(port, false));
            }
            if (portWidgets.isEmpty()) {
                portWidgets.add(new Padding(Insets.none()));
            }
            return portWidgets;
        }

        private Widget buildPort(NodePort port, boolean isInput) {
            var portColor = getPortColor(port.type());
            var connectedColor = port.isConnected() ? portColor : portColor.withA(0.5);

            return new MouseArea(
                widget -> widget
                    .clickCallback((x, y, button, modifiers) -> {
                        onPortClick.onPortClick(port, x, y);
                        return true;
                    })
                    .cursorStyle(CursorStyle.HAND),
                new Row(
                    isInput ? new Sized(8, 8, new Box(connectedColor)) : new Padding(Insets.none()),
                    new Padding(
                        Insets.horizontal(4),
                        new Label(
                            Component.literal(port.name()).withColor(0xAAAAAA)
                        )
                    ),
                    !isInput ? new Sized(8, 8, new Box(connectedColor)) : new Padding(Insets.none())
                )
            );
        }

        private Color getPortColor(PortType type) {
            return switch (type) {
                case STRING -> STRING_PORT_COLOR;
                case BOOLEAN -> BOOLEAN_PORT_COLOR;
                case NUMBER -> NUMBER_PORT_COLOR;
                case EXECUTION -> EXECUTION_PORT_COLOR;
                case ANY -> ANY_PORT_COLOR;
            };
        }
    }
}
