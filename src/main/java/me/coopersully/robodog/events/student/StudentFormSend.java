package me.coopersully.robodog.events.student;

import me.coopersully.Commons;
import me.coopersully.robodog.database.SQLiteManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

public class StudentFormSend extends ListenerAdapter {

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {

        if (!event.isFromGuild()) return;
        if (!event.getModalId().contains("student")) return;

        event.deferReply().setEphemeral(true).queue();

        Guild guild = event.getGuild();
        assert guild != null;

        User user = event.getUser();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder
                .setColor(Color.blue)
                .setTitle("Student Verification Request from " + user.getName())
                .setThumbnail(user.getEffectiveAvatarUrl())
                .setFooter("User ID: " + user.getId())
                .setTimestamp(Instant.now());

        String name = "";
        String email = "";
        String year = "";
        for (ModalMapping entry : event.getValues()) {

            String id = entry.getId();

            String key;
            String value = entry.getAsString();
            if (value.isEmpty()) continue;

            switch (id) {
                case "name" -> {
                    key = "Full Name";
                    name = value;
                }
                case "major" -> key = "Major";
                case "email" -> {
                    key = "School Email";
                    email = entry.getAsString();
                    value = "[" + entry.getAsString() + "](https://www2.samford.edu/onlineDir/)";

                    ResultSet students = SQLiteManager.getStudentByEmail(email);
                    try {
                        String userID = students.getString("id");
                        if (userID == null) throw new SQLException("User ID was null");

                        Member contradictor = guild.getMemberById(userID);
                        if (contradictor == null) throw new SQLException("Contradictor was null");

                        String studentName = students.getString("name");
                        if (studentName == null) throw new SQLException("Student Name was null");

                        embedBuilder.appendDescription(":exclamation: This email is already tied to " + contradictor.getAsMention() + "'s account; the name on the existing account is \"" + studentName + "\".");
                    } catch (SQLException ignored) {}
                }
                case "year" -> {
                    key = "Graduation Year";
                    year = value;
                }
                case "games" -> {
                    key = "What games do you frequently play?";
                    value = "```" + entry.getAsString() + "```";
                }
                default -> key = "Unknown field";
            }
            embedBuilder.addField(key, value, true);
        }

        Button accept = Button.success("ACCEPT_STUDENT_" + user.getId() + "_" + name + "_" + email + "_" + year, "Accept Request");
        Button deny = Button.danger("DENY_" + user.getId(), "Deny Request");

        Commons.wooshModal(event, guild, embedBuilder, accept, deny);

    }

}
