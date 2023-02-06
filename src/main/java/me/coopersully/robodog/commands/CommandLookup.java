package me.coopersully.robodog.commands;

import me.coopersully.Commons;
import me.coopersully.robodog.database.SQLiteManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;

public class CommandLookup extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        var command = event.getCommandPath();
        if (!command.contains("profile")) return;

        Member member = event.getMember();
        if (!member.hasPermission(Permission.ADMINISTRATOR)) {
            Commons.sendOrEdit(event, ":exclamation: You don't have permission to do that.");
            return;
        }

        // Ensure name is valid
        OptionMapping mapUser = event.getOption("user");
        if (mapUser == null) {
            Commons.sendOrEdit(event, "Please include all fields in your command; you didn't include the ``user`` field.");
            return;
        }
        User user = mapUser.getAsUser();

        EmbedBuilder embedBuilder = new EmbedBuilder();

        ResultSet resultSet = SQLiteManager.getUserByID(user.getId());
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            if (columnCount > 0) {
                while (resultSet.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        var key = metaData.getColumnName(i);
                        var value = resultSet.getString(i);
                        if (key.equalsIgnoreCase("seen")) {
                            embedBuilder
                                    .setFooter("Date Registered")
                                    .setTimestamp(Instant.ofEpochMilli( Long.parseLong(value) ));
                        } else {
                            embedBuilder.addField(
                                    metaData.getColumnName(i),
                                    ((value == null || value.isEmpty()) ? "``N/A``" : "``" + value + "``"),
                                    !key.equalsIgnoreCase("note")
                            );
                        }
                    }
                }
            } else {
                embedBuilder.setDescription("No information is known about this user.");
            }

            embedBuilder.setTitle(user.getName() + "'s verification profile").setThumbnail(user.getEffectiveAvatarUrl());

            Commons.sendOrEdit(event, "");
            Commons.sendOrEdit(event, embedBuilder.build());
            return;
        } catch (SQLException ignored) {
        }

        Commons.sendOrEdit(event, "That user doesn't seem to be registered quite yet.");
    }

}
