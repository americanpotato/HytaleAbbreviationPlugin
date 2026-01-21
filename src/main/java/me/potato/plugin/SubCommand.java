package me.potato.plugin;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;

public class SubCommand extends CommandBase {

    private final String pluginName;
    private final String pluginVersion;

    public SubCommand(String pluginName, String pluginVersion) {
        super("sub", "manage command substitutions");
        this.pluginName = pluginName;
        this.pluginVersion = pluginVersion;
        this.setAllowsExtraArguments(true);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext ctx) {
        String input = ctx.getInputString();
        String[] inputs = input.split(" ");

        if(input.startsWith(this.getName() + " add")) {
            String trimmedInput = ctx.getInputString().trim();

            int firstSlash = trimmedInput.indexOf('/');
            int secondSlash = trimmedInput.indexOf('/', firstSlash + 1);

            if (input.equals("sub add")) {
                sendHeaderMessage(ctx, "Add");
                ctx.sendMessage(Message.raw("Usage:"));
                sendCommandMessage(ctx, "/sub add <abbreviation> <original command>");
                ctx.sendMessage(Message.raw("Examples:"));
                sendCommandMessage(ctx, "/sub add /sky /tp ~ 170 ~");
                sendCommandMessage(ctx, "/sub add /xz $ $ /tp $ ~ $");
                return;
            }

            String abbreviation = trimmedInput.substring(firstSlash, secondSlash).trim();
            String originalCommand = trimmedInput.substring(secondSlash).trim();

            CMDSubstitutionPlugin.addSubstitution(abbreviation, originalCommand, this.pluginName, true);

            sendHeaderMessage(ctx, "Registered new command substitution: " + abbreviation + " -> " + originalCommand);
        } else if(input.startsWith(this.getName() + " remove")) {
            if(input.equals("sub remove")) {
                sendHeaderMessage(ctx, "Remove");
                ctx.sendMessage(Message.raw("Usage:"));
                sendCommandMessage(ctx, "/removesub <id>");
                ctx.sendMessage(Message.raw("Example:"));
                sendCommandMessage(ctx, "/removesub 1");
                ctx.sendMessage(Message.raw("Other notes:"));
                ctx.sendMessage(Message.raw("Use /sub list to get the ids for substitutions"));
                ctx.sendMessage(Message.raw("Removed commands exist in game until the server restarts"));
            } else if(inputs.length == 3) {
                int index = Integer.parseInt(inputs[2]) - 1;
                List<SubData> mappings = CMDSubstitutionPlugin.config.getMappings();
                SubData abr = mappings.get(index);
                CMDSubstitutionPlugin.config.removeSub(index);
                sendHeaderMessage(ctx, "Removed from config: " + abr.getSub() + " -> " + abr.getOriginal());
            }
        } else if(input.startsWith(this.getName() + " list")) {
            sendHeaderMessage(ctx, "List");
            List<SubData> mappings = CMDSubstitutionPlugin.config.getMappings();
            sendExistingCommandMessage(ctx, "id", "/examplesub", "/exampleoriginal");

            int index = 1;
            for(SubData abr : mappings) {
                sendExistingCommandMessage(ctx, index+"", abr.getSub(), abr.getOriginal());
                index++;
            }
        } else {
            sendHeaderMessage(ctx, "Abbreviations v" + this.pluginVersion);
            sendCommandMessage(ctx, "/" + this.getName() + " add");
            sendCommandMessage(ctx, "/" + this.getName() + " list");
            sendCommandMessage(ctx, "/" + this.getName() + " remove");
        }




    }

    private Color mainColor = Color.RED;

    public void sendHeaderMessage(CommandContext ctx, String msg) {
        ctx.sendMessage(Message.join(getPrefix(), Message.raw(msg)));
    }

    public void sendCommandMessage(CommandContext ctx, String msg) {
        ctx.sendMessage(Message.join(getCommandPrefix(), Message.raw(msg)));
    }

    public void sendExistingCommandMessage(CommandContext ctx, String id, String abr, String old) {
        ctx.sendMessage(Message.join(
                Message.raw("["), Message.raw(id).color(Color.pink), Message.raw("] "),
                Message.raw(abr),
                Message.raw(" -> ").color(Color.RED),
                Message.raw(old)));
    }

    private Message getPrefix() {
        return Message.raw("[s] ").color(mainColor);
    }

    private Message getCommandPrefix() {
        return Message.raw("> ").color(mainColor);
    }
}