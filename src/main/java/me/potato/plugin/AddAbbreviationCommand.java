package me.potato.plugin;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;

import javax.annotation.Nonnull;
import java.util.List;

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
            ctx.sendMessage(Message.raw("Usage: /addsub <abbreviation> <original command>"));
            ctx.sendMessage(Message.raw("Example: /addsub /sky /tp ~ 170 ~"));
            ctx.sendMessage(Message.raw("Advanced Example: /addsub /xz $ $ /tp $ ~ $"));
            ctx.sendMessage(Message.raw("then \"/xz 0 100\" tps the player to x:0, z:100"));
            ctx.sendMessage(Message.raw("Also see - /listsub and /removesub "));
            return;
        }

        String abbreviation = input.substring(firstSlash, secondSlash).trim();
        String originalCommand = input.substring(secondSlash).trim();

        CMDSubstitutionPlugin.addSubstitution(abbreviation, originalCommand, this.pluginName, true);

        ctx.sendMessage(Message.raw("Registered new command substitution: " + abbreviation + " -> " + originalCommand));
    }
}