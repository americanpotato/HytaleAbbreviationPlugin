package me.potato.plugin;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Substitutes one command for another
 */
public class ListAbbreviationCommand extends CommandBase {

    private final String pluginName;
    private final String pluginVersion;

    public ListAbbreviationCommand(String pluginName, String pluginVersion) {
        super("listsub", "list abbreviations");
        this.pluginName = pluginName;
        this.pluginVersion = pluginVersion;
    }

    @Override
    protected void executeSync(@Nonnull CommandContext ctx) {
        List<List<String>> mappings = CMDSubstitutionPlugin.config.getMappings();

        int index = 1;
        for(List<String> abr : mappings) {
            ctx.sendMessage(Message.raw("[" + index + "] " + abr.getFirst() + " -> " + abr.getLast()));
            index++;
        }
    }
}