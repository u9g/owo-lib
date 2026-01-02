package io.wispforest.uwu.client.nodelang;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.TextAreaComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.uwu.nodelang.NodeLanguageRuntime;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class NodeLanguageScreen extends BaseOwoScreen<FlowLayout> {

    private TextAreaComponent editor;
    private TextBoxComponent fileName;
    private LabelComponent status;

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, UIContainers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        var runtime = NodeLanguageRuntime.get();

        rootComponent.surface(Surface.VANILLA_TRANSLUCENT)
            .padding(Insets.of(8))
            .gap(6)
            .horizontalAlignment(HorizontalAlignment.FILL);

        this.fileName = UIComponents.textBox(Sizing.fill(100), runtime.scriptPath().getFileName().toString());
        this.fileName.setMaxLength(64);

        this.editor = UIComponents.textArea(Sizing.fill(100), Sizing.fixed(180), runtime.scriptAsJson());
        this.editor.displayCharCount(true).maxLines(64);

        this.status = UIComponents.label(Component.literal(""));

        var fileRow = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.content())
            .gap(6)
            .child(UIComponents.label(Component.literal("Node file")))
            .child(this.fileName);

        var buttons = UIContainers.horizontalFlow(Sizing.content(), Sizing.content())
            .gap(6)
            .child(UIComponents.button(Component.literal("Load"), button -> this.loadScript()))
            .child(UIComponents.button(Component.literal("Save"), button -> this.saveScript()))
            .child(UIComponents.button(Component.literal("Reload From Disk"), button -> {
                runtime.reload();
                this.editor.setValue(runtime.scriptAsJson());
                this.status.text(Component.literal("Reloaded current script"));
            }));

        rootComponent
            .child(UIComponents.label(Component.literal("Node-based language editor (allow chat, HUD, tick events)")))
            .child(fileRow)
            .child(UIComponents.label(Component.literal("Scripts live in the config folder. allowChat nodes use includes + allow flag.")))
            .child(this.editor)
            .child(buttons)
            .child(this.status);
    }

    private Path resolvePath() {
        var configDir = FabricLoader.getInstance().getConfigDir();
        var name = this.fileName.getValue().isBlank() ? "owo-node-language.json" : this.fileName.getValue();
        return configDir.resolve(name);
    }

    private void loadScript() {
        var runtime = NodeLanguageRuntime.get();
        try {
            var text = runtime.loadText(this.resolvePath());
            this.editor.setValue(text);
            this.status.text(Component.literal("Loaded " + this.fileName.getValue()));
        } catch (Exception e) {
            this.status.text(Component.literal("Failed to load: " + e.getMessage()));
        }
    }

    private void saveScript() {
        var runtime = NodeLanguageRuntime.get();
        try {
            runtime.saveText(this.resolvePath(), this.editor.getValue());
            this.status.text(Component.literal("Saved " + this.fileName.getValue()));
        } catch (Exception e) {
            this.status.text(Component.literal("Failed to save: " + e.getMessage()));
        }
    }
}
