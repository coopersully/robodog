package me.coopersully.robodog.commands;

import me.coopersully.Commons;
import me.coopersully.robodog.database.SQLiteManager;
import me.coopersully.robodog.events.forms.FormCommons;
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
import java.time.Instant;

public class CommandProfile extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        var command = event.getCommandPath();
        if (!command.contains("profile")) return;

        Member member = event.getMember();
        if (!member.hasPermission(Permission.ADMINISTRATOR)) {
            Commons.sendOrEdit(event, Commons.notifFail("You don't have permission to do that."));
            return;
        }

        event.deferReply().setEphemeral(true).queue();

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

            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    var key = metaData.getColumnName(i);
                    var value = resultSet.getString(i);
                    if (key.equalsIgnoreCase("seen")) {
                        embedBuilder
                                .setFooter("Date Registered")
                                .setTimestamp(Instant.ofEpochMilli(Long.parseLong(value)));
                    }
                    else if (key.equalsIgnoreCase("type")) {
                        embedBuilder.addField(
                                "Position",
                                "``" + Commons.getNameByPosition(Integer.parseInt(value)).toUpperCase() + "``",
                                true
                        );
                    } else if (key.equalsIgnoreCase("note")) {
                        embedBuilder.addField(
                                "Notes",
                                ((value == null || value.isEmpty()) ? "```N/A```" : "```" + value + "```"),
                                false
                        );
                    } else {
                        embedBuilder.addField(
                                FormCommons.cypher.get(key),
                                ((value == null || value.isEmpty()) ? "``N/A``" : "``" + value + "``"),
                                true
                        );
                    }
                }
            }

            if (embedBuilder.getFields().isEmpty()) {
                Commons.sendOrEdit(event, Commons.notifFail("No information is known about this user."));
                return;
            }

            embedBuilder.setTitle(user.getName() + "'s verification profile").setThumbnail(user.getEffectiveAvatarUrl());
            Commons.sendOrEdit(event, embedBuilder.build());
        } catch (SQLException ignored) {
            Commons.sendOrEdit(event, "Something went wrong- please contact an administrator and/or the bot developer.");
        }
    }

}
