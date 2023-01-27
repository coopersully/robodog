package me.coopersully.robodog.commands;

import me.coopersully.Commons;
import me.coopersully.robodog.database.SQLiteManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(guild.getName() + "'s assigned positions");

        var resultSet = SQLiteManager.getGuildByID(guild.getId());
        Role unverified, verified, student, alumni, faculty, guest;
        try {
            var unverifiedID = resultSet.getString("r_unverified");
            if (unverifiedID != null) {
                unverified = guild.getRoleById(unverifiedID);
                embedBuilder.appendDescription("**Unverified: ** " + unverified.getAsMention() + "\n");
            } else {
                embedBuilder.appendDescription("**Unverified: **  ``None``\n");
            }

            var verifiedID = resultSet.getString("r_verified");
            if (verifiedID != null) {
                verified = guild.getRoleById(verifiedID);
                embedBuilder.appendDescription("**Verified: ** " + verified.getAsMention() + "\n");
            } else {
                embedBuilder.appendDescription("**Verified: **  ``None``\n");
            }

            var studentID = resultSet.getString("r_student");
            if (studentID != null) {
                student = guild.getRoleById(studentID);
                embedBuilder.appendDescription("**Verified: ** " + student.getAsMention() + "\n");
            } else {
                embedBuilder.appendDescription("**Student: **  ``None``\n");

            }

            var alumniID = resultSet.getString("r_alumni");
            if (alumniID != null) {
                alumni = guild.getRoleById(alumniID);
                embedBuilder.appendDescription("**Alumni: ** " + alumni.getAsMention() + "\n");
            } else {
                embedBuilder.appendDescription("**Alumni: **  ``None``\n");
            }

            var facultyID = resultSet.getString("r_faculty");
            if (facultyID != null) {
                faculty = guild.getRoleById(facultyID);
                embedBuilder.appendDescription("**Faculty: ** " + faculty.getAsMention() + "\n");
            } else {
                embedBuilder.appendDescription("**Faculty: **  ``None``\n");
            }

            var guestID = resultSet.getString("r_guest");
            if (guestID != null) {
                guest = guild.getRoleById(guestID);
                embedBuilder.appendDescription("**Guest: ** " + guest.getAsMention() + "\n");
            } else {
                embedBuilder.appendDescription("**Guest: **  ``None``\n");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        event.replyEmbeds(embedBuilder.build()).queue();
    }

}
