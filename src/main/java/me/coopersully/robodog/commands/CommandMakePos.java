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

        // Retrieve or create 'faculty' positional role
        Role faculty = SQLiteManager.getGuildFacultyRole(guild);
        if (faculty == null) {
            faculty = guild.createRole()
                    .setName("Faculty")
                    .setPermissions(Permission.EMPTY_PERMISSIONS)
                    .complete();
            SQLiteManager.setGuildPosition(guild.getId(), "faculty", faculty);
        }

        // Retrieve or create 'alumni' positional role
        Role alumni = SQLiteManager.getGuildAlumniRole(guild);
        if (alumni == null) {
            alumni = guild.createRole()
                    .setName("Alumni")
                    .setPermissions(Permission.EMPTY_PERMISSIONS)
                    .complete();
            SQLiteManager.setGuildPosition(guild.getId(), "alumni", alumni);
        }
        // Retrieve or create 'student' positional role
        Role student = SQLiteManager.getGuildStudentRole(guild);
        if (student == null) {
            student = guild.createRole()
                    .setName("Student")
                    .setPermissions(Permission.EMPTY_PERMISSIONS)
                    .complete();
            SQLiteManager.setGuildPosition(guild.getId(), "student", student);
        }

        // Retrieve or create 'guest' positional role
        Role guest = SQLiteManager.getGuildGuestRole(guild);
        if (guest == null) {
            guest = guild.createRole()
                    .setName("Guest")
                    .setPermissions(Permission.EMPTY_PERMISSIONS)
                    .complete();
            SQLiteManager.setGuildPosition(guild.getId(), "guest", guest);
        }

        // Retrieve or create 'verified' positional role
        Role verified = SQLiteManager.getGuildVerifiedRole(guild);
        if (verified == null) {
            verified = guild.createRole()
                    .setName("Verified")
                    .setPermissions(Permission.EMPTY_PERMISSIONS)
                    .complete();
            SQLiteManager.setGuildPosition(guild.getId(), "verified", verified);
        }

        // Retrieve or create 'unverified' positional role
        Role unverified = SQLiteManager.getGuildUnverifiedRole(guild);
        if (unverified == null) {
            unverified = guild.createRole()
                    .setName("Unverified")
                    .setPermissions(Permission.EMPTY_PERMISSIONS)
                    .complete();
            SQLiteManager.setGuildPosition(guild.getId(), "unverified", unverified);
        }

        event.reply("Automatically filled all positions. Check them with ``/positions``").setEphemeral(true).queue();
    }

}
