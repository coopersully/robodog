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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
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
            unverified = getRole(guild, resultSet, "r_unverified");
            embedBuilder.appendDescription("**Unverified: ** " + (unverified == null ? "``None``" : unverified.getAsMention()) + "\n");

            verified = getRole(guild, resultSet, "r_verified");
            embedBuilder.appendDescription("**Verified: ** " + (verified == null ? "``None``" : verified.getAsMention()) + "\n");

            student = getRole(guild, resultSet, "r_student");
            embedBuilder.appendDescription("**Student: ** " + (student == null ? "``None``" : student.getAsMention()) + "\n");

            alumni = getRole(guild, resultSet, "r_alumni");
            embedBuilder.appendDescription("**Alumni: ** " + (alumni == null ? "``None``" : alumni.getAsMention()) + "\n");

            faculty = getRole(guild, resultSet, "r_faculty");
            embedBuilder.appendDescription("**Faculty: ** " + (faculty == null ? "``None``" : faculty.getAsMention()) + "\n");

            guest = getRole(guild, resultSet, "r_guest");
            embedBuilder.appendDescription("**Guest: ** " + (guest == null ? "``None``" : guest.getAsMention()) + "\n");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        event.replyEmbeds(embedBuilder.build()).queue();
    }

    private @Nullable Role getRole(Guild guild, @NotNull ResultSet resultSet, String roleName) throws SQLException {
        var roleID = resultSet.getString(roleName);
            if (roleID != null) return guild.getRoleById(roleID);
        return null;
    }

}
