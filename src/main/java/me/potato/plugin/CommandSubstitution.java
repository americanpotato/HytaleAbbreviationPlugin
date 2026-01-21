package me.potato.plugin;

import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;

import javax.annotation.Nonnull;

/**
 * Substitutes one command for another
 */
public class CommandSubstitution extends CommandBase {

    private final String pluginName;
    private final String pluginVersion;
    private final String originalCommand;

    public CommandSubstitution(String originalCommand, String replacementCommand, String pluginName, String pluginVersion) {
        super(replacementCommand.substring(1), "abbreviation for " + originalCommand);
        this.originalCommand = originalCommand;
        this.pluginName = pluginName;
        this.pluginVersion = pluginVersion;
        this.setAllowsExtraArguments(true);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext ctx) {
//        String[] inputs = ctx.getInputString().split(" ");

        if(ctx.isPlayer()) {
            SubData data = CMDSubstitutionPlugin.getCorrectSubData(ctx.getInputString(), ctx);

            if(data == null) {
                return;
            }

            for(String s : data.getExecuteStrings(ctx.getInputString(), data.getOriginal())) {
                HytaleServer.get().getCommandManager().handleCommand(ctx.sender(), s.substring(1));
            }
        }
    }
}