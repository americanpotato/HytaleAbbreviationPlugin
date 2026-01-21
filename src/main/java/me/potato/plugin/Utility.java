package me.potato.plugin;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;

import java.awt.*;

public class Utility {

    public static void sendError(CommandContext ctx, String msg) {
        ctx.sendMessage(Message.raw("Error: " + msg).color(Color.RED));
    }
}
