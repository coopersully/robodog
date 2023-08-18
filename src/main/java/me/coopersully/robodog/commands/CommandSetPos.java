package me.coopersully.robodog.commands;

import me.coopersully.robodog.Commons;
import me.coopersully.robodog.database.SQLiteManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class CommandSetPos extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        var command = event.getCommandPath();
        if (!command.contains("setpos")) return;

        Guild guild = event.getGuild();
        if (guild == null) return;

        if (!event.getMember().hasPermission(Permission.MANAGE_PERMISSIONS)) {
            Commons.sendOrEdit(event, Commons.notifFail("You don't have permission to do that."));
            return;
        }

        var subcommand = event.getSubcommandName();

        OptionMapping mapRole = event.getOption("role");
        if (mapRole == null) {
            Commons.sendOrEdit(event, "Please include all fields in your command; you didn't include the ``name`` field.");
            return;
        }
        Role role = mapRole.getAsRole();
        SQLiteManager.setGuildPosition(guild.getId(), subcommand, role);

        event.reply("Successfully set the ``" + subcommand + "`` position to " + role.getName()).setEphemeral(true).queue();
    }

}
