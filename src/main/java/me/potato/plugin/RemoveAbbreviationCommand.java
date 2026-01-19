package me.potato.plugin;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * Substitutes one command for another
 */
public class RemoveAbbreviationCommand extends CommandBase {

    private final String pluginName;
    private final String pluginVersion;

    public RemoveAbbreviationCommand(String pluginName, String pluginVersion) {
        super("removesub", "remove abbreviations");
        this.pluginName = pluginName;
        this.pluginVersion = pluginVersion;
        this.setAllowsExtraArguments(true);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext ctx) {
        String[] inputs = ctx.getInputString().split(" ");

        if(inputs.length == 1) {
            ctx.sendMessage(Message.raw("Usage /removesub <id>"));
            ctx.sendMessage(Message.raw("Example: /removesub 1"));
            ctx.sendMessage(Message.raw("Use /listsub to get the ids for substitutions"));
            ctx.sendMessage(Message.raw("Removed commands exist in game until the server restarts"));
        } else if(inputs.length == 2) {
            int index = Integer.parseInt(inputs[1]) - 1;
            List<List<String>> mappings = CMDSubstitutionPlugin.config.getMappings();
            List<String> abr = mappings.get(index);
            CMDSubstitutionPlugin.config.removeSub(index);
            ctx.sendMessage(Message.raw("Removed from config: " + abr.getFirst() + " -> " + abr.getLast()));
        }
    }
}