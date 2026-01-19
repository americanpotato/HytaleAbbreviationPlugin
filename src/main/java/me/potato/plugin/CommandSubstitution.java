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
        this.setPermissionGroups("abbreviations");
        this.originalCommand = originalCommand;
        this.pluginName = pluginName;
        this.pluginVersion = pluginVersion;
        this.setAllowsExtraArguments(true);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext ctx) {
        String[] inputs = ctx.getInputString().split(" ");

        if(ctx.isPlayer()) {
            if(inputs.length == 1) {
                HytaleServer.get().getCommandManager().handleCommand(ctx.sender(), this.originalCommand.substring(1));
            } else {
                String command = CMDSubstitutionPlugin.argsToConfigList.get(CMDSubstitutionPlugin.getRightOne("/" + inputs[0], inputs.length - 1)).getLast().replace("/","");
                int index = 1;
                while(command.contains("$")) {
                    command = command.replaceFirst("\\$", inputs[index]);
                    index++;
                }
                HytaleServer.get().getCommandManager().handleCommand(ctx.sender(), command);
            }
        }
    }
}