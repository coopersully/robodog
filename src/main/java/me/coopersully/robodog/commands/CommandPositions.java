package me.coopersully.robodog.commands;

import me.coopersully.robodog.Commons;
import me.coopersully.robodog.database.SQLiteManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class CommandPositions extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        var command = event.getCommandPath();
        if (!command.contains("positions")) return;

        Guild guild = event.getGuild();
        if (guild == null) return;

        if (!event.getMember().hasPermission(Permission.MANAGE_PERMISSIONS)) {
            Commons.sendOrEdit(event, Commons.notifFail("You don't have permission to do that."));
            return;
        }

        event.deferReply().setEphemeral(true).queue();

        var subcommand = event.getSubcommandName();
        assert subcommand != null;

        switch (subcommand) {
            case "list" -> list(event, guild);
            case "autofill" -> autofill(event, guild);
            default -> throw new IllegalStateException("Unexpected value: " + subcommand);
        }
    }

    private static void list(SlashCommandInteractionEvent event, @NotNull Guild guild) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder
                .setColor(Color.GREEN)
                .setTitle(guild.getName() + "'s assigned positions");

        Role unverified, verified, student, alumni, faculty, guest;

        unverified = SQLiteManager.getGuildUnverifiedRole(guild);
        embedBuilder.appendDescription("**Unverified: ** " + (unverified == null ? "``None``" : unverified.getAsMention()) + "\n");

        verified = SQLiteManager.getGuildVerifiedRole(guild);
        embedBuilder.appendDescription("**Verified: ** " + (verified == null ? "``None``" : verified.getAsMention()) + "\n");

        student = SQLiteManager.getGuildStudentRole(guild);
        embedBuilder.appendDescription("**Student: ** " + (student == null ? "``None``" : student.getAsMention()) + "\n");

        alumni = SQLiteManager.getGuildAlumniRole(guild);
        embedBuilder.appendDescription("**Alumni: ** " + (alumni == null ? "``None``" : alumni.getAsMention()) + "\n");

        faculty = SQLiteManager.getGuildFacultyRole(guild);
        embedBuilder.appendDescription("**Faculty: ** " + (faculty == null ? "``None``" : faculty.getAsMention()) + "\n");

        guest = SQLiteManager.getGuildGuestRole(guild);
        embedBuilder.appendDescription("**Guest: ** " + (guest == null ? "``None``" : guest.getAsMention()) + "\n");

        Commons.sendOrEdit(event, embedBuilder.build());
    }

    private static void autofill(@NotNull SlashCommandInteractionEvent event, @NotNull Guild guild) {
        // Retrieve or create 'faculty' positional role
        getOrCreateRole(guild, "Faculty", "faculty");

        // Retrieve or create 'alumni' positional role
        getOrCreateRole(guild, "Alumni", "alumni");

        // Retrieve or create 'student' positional role
        getOrCreateRole(guild, "Student", "student");

        // Retrieve or create 'guest' positional role
        getOrCreateRole(guild, "Guest", "guest");

        // Retrieve or create 'verified' positional role
        getOrCreateRole(guild, "Verified", "verified");

        // Retrieve or create 'unverified' positional role
        getOrCreateRole(guild, "Unverified", "unverified");

        Commons.sendOrEdit(event, Commons.notifSuccess("Automatically filled all positions. Check them with ``/positions list``"));
    }

    private static @Nullable Role getRoleByName(@NotNull Guild guild, String name) {
        var roles = guild.getRolesByName(name, true);
        if (roles.isEmpty()) return null;
        return roles.get(0);
    }

    private static void getOrCreateRole(Guild guild, String roleName, String dbKey) {
        Role role = SQLiteManager.getGuildRoleByNamespace(guild, dbKey);
        if (role != null) return;

        role = getRoleByName(guild, roleName.toLowerCase());
        if (role == null) {
            role = guild.createRole()
                    .setName(roleName)
                    .setPermissions(Permission.EMPTY_PERMISSIONS)
                    .complete();
        }
        SQLiteManager.setGuildPosition(guild.getId(), dbKey, role);
    }
}
