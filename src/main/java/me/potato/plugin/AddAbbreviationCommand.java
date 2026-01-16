package me.potato.plugin;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;

import javax.annotation.Nonnull;

/**
 * Substitutes one command for another
 */
public class AddAbbreviationCommand extends CommandBase {

    private final String pluginName;
    private final String pluginVersion;

    public AddAbbreviationCommand(String pluginName, String pluginVersion) {
        super("addsub", "add abbreviations for command");
        this.pluginName = pluginName;
        this.pluginVersion = pluginVersion;
        this.setAllowsExtraArguments(true);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext ctx) {
        String input = ctx.getInputString().trim();

        int firstSlash = input.indexOf('/');
        int secondSlash = input.indexOf('/', firstSlash + 1);

        if (secondSlash == -1 || firstSlash == -1) {
            ctx.sendMessage(Message.raw("Usage: /addsub <abbreviation> <original command>, example: /addsub /goup /tp ~ 100 ~"));
            return;
        }

        String abbreviation = input.substring(firstSlash, secondSlash).trim();
        String originalCommand = input.substring(secondSlash).trim();

        CommandSubstitution substitution = new CommandSubstitution(
                originalCommand,
                abbreviation,
                this.getName(),
                CMDSubstitutionPlugin.version
        );

        CMDSubstitutionPlugin.registry.registerCommand(substitution);
        CMDSubstitutionPlugin.config.addToConfig(abbreviation, originalCommand);

        ctx.sendMessage(Message.raw("Registered new command substitution: " + abbreviation + " -> " + originalCommand));
    }
}