package io.wispforest.uwu.nodelang;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.List;

public final class NodeLanguageRuntime {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final NodeScript DEFAULT_SCRIPT = new NodeScript(
        List.of(new AllowChatNode("sample-allow-chat", "hello", true)),
        List.of(new HudNode("sample-hud", "Node language active", 4, 4, 0xFFFFFF)),
        List.of(new TickActionNode("sample-tick", 200, "Node tick event fired"))
    );

    private static final NodeLanguageRuntime INSTANCE = new NodeLanguageRuntime();

    private Path scriptPath;
    private NodeScript script;
    private FileTime lastLoaded;

    private NodeLanguageRuntime() {
        this.scriptPath = FabricLoader.getInstance().getConfigDir().resolve("owo-node-language.json");
        this.script = NodeScript.empty();
        this.reload();
    }

    public static NodeLanguageRuntime get() {
        return INSTANCE;
    }

    public synchronized void setScriptPath(Path path) {
        if (path != null) this.scriptPath = path;
    }

    public synchronized Path scriptPath() {
        return this.scriptPath;
    }

    public synchronized NodeScript script() {
        return this.script;
    }

    public synchronized String scriptAsJson() {
        return GSON.toJson(this.script);
    }

    public synchronized String loadText(Path path) throws IOException, JsonParseException {
        this.setScriptPath(path);

        if (!Files.exists(path)) {
            this.script = DEFAULT_SCRIPT;
            this.persist();
            return this.scriptAsJson();
        }

        var text = Files.readString(path);
        this.script = this.parse(text);
        this.lastLoaded = Files.getLastModifiedTime(path);
        return text;
    }

    public synchronized void saveText(Path path, String text) throws IOException, JsonParseException {
        this.setScriptPath(path);
        this.script = this.parse(text);
        this.persist();
    }

    public synchronized boolean allowChatMessage(String message) {
        var content = message == null ? "" : message;
        Boolean decision = null;

        for (var node : this.script.allowChatNodes()) {
            if (!node.includes().isEmpty() && !content.contains(node.includes())) continue;

            if (!node.allow()) return false;
            decision = true;
        }

        return decision == null || decision;
    }

    public synchronized List<HudNode> hudNodes() {
        return this.script.hudNodes();
    }

    public synchronized List<TickActionNode> tickNodes() {
        return this.script.tickNodes();
    }

    public synchronized boolean reloadIfChanged() {
        if (!Files.exists(this.scriptPath)) return false;

        try {
            var currentTime = Files.getLastModifiedTime(this.scriptPath);
            if (this.lastLoaded == null || !currentTime.equals(this.lastLoaded)) {
                this.loadText(this.scriptPath);
                return true;
            }
        } catch (IOException | JsonParseException ignored) {
            // ignore reload errors to avoid crashing when the user is midway through editing
        }

        return false;
    }

    public synchronized void reload() {
        try {
            this.loadText(this.scriptPath);
        } catch (IOException | JsonParseException e) {
            this.script = DEFAULT_SCRIPT;
        }
    }

    private NodeScript parse(String text) throws JsonParseException {
        var parsed = GSON.fromJson(text, NodeScript.class);
        return parsed != null ? parsed : NodeScript.empty();
    }

    private void persist() throws IOException {
        Files.createDirectories(this.scriptPath.getParent());
        Files.writeString(this.scriptPath, this.scriptAsJson());
        this.lastLoaded = Files.getLastModifiedTime(this.scriptPath);
    }
}
