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
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CommandLookup extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        var command = event.getCommandPath();
        if (!command.contains("lookup")) return;

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

        ResultSet student = SQLiteManager.getStudentByID(user.getId());
        try {
            String name = student.getString("name");
            if (name == null) throw new SQLException("Provided name was null");

            String email = student.getString("email");
            if (email == null) throw new SQLException("Provided email was null");

            String year = student.getString("year");
            if (year == null) throw new SQLException("Provided year was null");

            String seen = student.getString("seen");
            if (seen == null) throw new SQLException("Provided seen was null");

            String note = Commons.blankIfNull(student.getString("note"));

            long seenMilliseconds = Long.parseLong(seen);
            SimpleDateFormat simpleDateFormat =
                    new SimpleDateFormat("MMM dd, yyyy HH:mm");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT-5"));
            Date seenDate = new Date(seenMilliseconds);

            embedBuilder
                    .setTitle(user.getName() + "'s verification profile")
                    .addField("Full Name", name, true)
                    .addField("Email Address", email, true)
                    .addField("Graduation Year", "``" + year + "``", true)
//                    .addField("Registration Date", "``" + simpleDateFormat.format(seenDate) + "``", true)
                    .addField("Note", "```" + note + "```", true)
                    .setThumbnail(user.getEffectiveAvatarUrl())
                    .setFooter("Registration Date")
                    .setTimestamp(seenDate.toInstant());

            Commons.sendOrEdit(event, "");
            Commons.sendOrEdit(event, embedBuilder.build());
            return;
        } catch (SQLException ignored) {}

        ResultSet guest = SQLiteManager.getGuestByID(user.getId());
        try {
            String name = guest.getString("name");
            if (name == null) throw new SQLException("Provided name was null");

            String business = guest.getString("business");
            if (business == null) throw new SQLException("Provided business was null");

            String seen = guest.getString("seen");
            if (seen == null) throw new SQLException("Provided seen was null");

            String note = Commons.blankIfNull(guest.getString("note"));

            long seenMilliseconds = Long.parseLong(seen);
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
            Date seenDate = new Date(seenMilliseconds);

            embedBuilder
                    .setTitle(user.getName() + "'s verification profile")
                    .addField("Full Name", name, true)
                    .addField("Association", business, true)
                    .addField("Registration Date", "``" + sdf.format(seenDate) + "``", true)
                    .addField("Note", "```" + Commons.blankIfNull(note) + "```", true)
                    .setThumbnail(user.getEffectiveAvatarUrl());

            Commons.sendOrEdit(event, "");
            Commons.sendOrEdit(event, embedBuilder.build());
            return;
        } catch (SQLException ignored) {}

        Commons.sendOrEdit(event, "That user doesn't seem to be registered quite yet.");
    }

}
