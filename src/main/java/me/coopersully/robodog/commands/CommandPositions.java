package me.coopersully.robodog.commands;

import me.coopersully.Commons;
import me.coopersully.robodog.database.SQLiteManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandPositions extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        var command = event.getCommandPath();
        if (!command.contains("positions")) return;

        Guild guild = event.getGuild();
        if (guild == null) return;

        if (!event.getMember().hasPermission(Permission.MANAGE_PERMISSIONS)) {
            Commons.sendOrEdit(event, ":question: You don't have permission to do that.");
            return;
        }

        event.deferReply().setEphemeral(true).queue();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(guild.getName() + "'s assigned positions");

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
}
