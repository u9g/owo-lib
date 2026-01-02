package io.wispforest.owo.nodescript.ui;

import io.wispforest.owo.braid.core.BraidScreen;
import io.wispforest.owo.nodescript.model.NodeGraph;
import org.jetbrains.annotations.Nullable;

/**
 * Screen for the node-based visual scripting editor.
 */
public class NodeEditorScreen extends BraidScreen {

    public NodeEditorScreen(@Nullable NodeGraph graph) {
        super(new Settings(), new NodeEditorWidget(graph));
        this.settings.shouldPause = false;
    }

    public NodeEditorScreen() {
        this(null);
    }
}
