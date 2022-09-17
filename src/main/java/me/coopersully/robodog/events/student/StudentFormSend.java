package me.coopersully.robodog.events.student;

import me.coopersully.Commons;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

import java.awt.*;
import java.time.Instant;

public class StudentFormSend extends ListenerAdapter {

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {

        if (!event.isFromGuild()) return;
        if (!event.getModalId().contains("student")) return;

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

        Button accept = Button.success("ACCEPT_STUDENT_" + user.getId(), "Accept Request");
        Button deny = Button.danger("DENY_" + user.getId(), "Deny Request");

        for (ModalMapping entry : event.getValues()) {

            String id = entry.getId();

            String key;
            String value = entry.getAsString();
            if (value.isEmpty()) continue;

            switch (id) {
                case "name" -> key = "Full Name";
                case "major" -> key = "Major";
                case "email" -> {
                    key = "School Email";
                    value = "[" + entry.getAsString() + "](https://www2.samford.edu/onlineDir/)";
                }
                case "games" -> {
                    key = "What games do you frequently play?";
                    value = "```" + entry.getAsString() + "```";
                }
                default -> key = "Unknown field";
            }
            embedBuilder.addField(key, value, true);
        }

        Commons.wooshModal(event, guild, embedBuilder, accept, deny);

    }

}
