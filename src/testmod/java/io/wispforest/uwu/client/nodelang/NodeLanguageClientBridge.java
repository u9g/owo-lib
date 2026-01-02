package io.wispforest.uwu.client.nodelang;

import io.wispforest.uwu.nodelang.NodeLanguageRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public final class NodeLanguageClientBridge {

    private NodeLanguageClientBridge() {}

    public static void renderHud(GuiGraphics graphics, float tickDelta) {
        var runtime = NodeLanguageRuntime.get();
        var font = Minecraft.getInstance().font;

        for (var node : runtime.hudNodes()) {
            graphics.drawString(font, Component.literal(node.text()), node.x(), node.y(), node.color(), true);
        }
    }

    public static void onClientTick(Minecraft client) {
        var runtime = NodeLanguageRuntime.get();
        runtime.reloadIfChanged();

        if (client.level == null || client.player == null) return;

        for (var node : runtime.tickNodes()) {
            if (node.actionText().isEmpty()) continue;

            if (client.level.getGameTime() % node.interval() == 0) {
                client.player.displayClientMessage(Component.literal(node.actionText()), true);
            }
        }
    }
}
