package me.coopersully.robodog.commands;

import me.coopersully.Commons;
import me.coopersully.robodog.database.SQLiteManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

public class CommandMakePos extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        var command = event.getCommandPath();
        if (!command.contains("makepos")) return;

        Guild guild = event.getGuild();
        if (guild == null) return;

        if (!event.getMember().hasPermission(Permission.MANAGE_PERMISSIONS)) {
            Commons.sendOrEdit(event, ":question: You don't have permission to do that.");
            return;
        }

        var faculty = getOrCreateRole(guild, "Faculty");
        SQLiteManager.setGuildPosition(guild.getId(), "faculty", faculty);

        var alumni = getOrCreateRole(guild, "Alumni");
        SQLiteManager.setGuildPosition(guild.getId(), "alumni", alumni);

        var student = getOrCreateRole(guild, "Student");
        SQLiteManager.setGuildPosition(guild.getId(), "student", student);

        var guest = getOrCreateRole(guild, "Guest");
        SQLiteManager.setGuildPosition(guild.getId(), "guest", guest);

        var verified = getOrCreateRole(guild, "Verified");
        SQLiteManager.setGuildPosition(guild.getId(), "verified", verified);

        var unverified = getOrCreateRole(guild, "Unverified");
        SQLiteManager.setGuildPosition(guild.getId(), "unverified", unverified);

        event.reply("Automatically filled all positions. Check them with ``/positions``").setEphemeral(true).queue();
    }

  public static Role getOrCreateRole(@NotNull Guild guild, String name) {
    Role role = guild.getRolesByName(name, true).stream().findFirst().orElse(null);
    if (role != null) return role;

    role = guild.createRole().setName(name).setPermissions(Permission.EMPTY_PERMISSIONS).complete();
    return role;
  }

}
