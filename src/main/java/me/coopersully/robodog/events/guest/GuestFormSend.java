package me.coopersully.robodog.events.guest;

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

public class GuestFormSend extends ListenerAdapter {

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {

        if (!event.isFromGuild()) return;
        if (!event.getModalId().contains("guest")) return;

        event.deferReply().setEphemeral(true).queue();

        Guild guild = event.getGuild();
        assert guild != null;

        User user = event.getUser();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder
                .setColor(Color.yellow)
                .setTitle("Guest Access Request from " + user.getName())
                .setThumbnail(user.getEffectiveAvatarUrl())
                .setFooter("User ID: " + user.getId())
                .setTimestamp(Instant.now());

        String name = "";
        String business = "";
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
                case "org" -> {
                    key = "Association";
                    business = value;
                }
                case "purpose" -> {
                    key = "What's the purpose of your visit?";
                    value = "```" + entry.getAsString() + "```";
                }
                default -> key = "Unknown field";
            }
            embedBuilder.addField(key, value, true);
        }

        Button accept = Button.success("ACCEPT_GUEST_" + user.getId() + "_" + name + "_" + business, "Accept Request");
        Button deny = Button.danger("DENY_" + user.getId(), "Deny Request");


        Commons.wooshModal(event, guild, embedBuilder, accept, deny);

    }

}
