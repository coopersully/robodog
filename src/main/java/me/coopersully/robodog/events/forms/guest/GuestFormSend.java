package me.coopersully.robodog.events.forms.guest;

import me.coopersully.robodog.Commons;
import me.coopersully.robodog.database.RegisteredUser;
import me.coopersully.robodog.events.forms.FormCommons;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;

public class GuestFormSend extends ListenerAdapter {

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {

        if (!event.isFromGuild()) return;
        if (!event.getModalId().contains("guest")) return;

        event.deferReply().setEphemeral(true).queue();

        Guild guild = event.getGuild();
        assert guild != null;

        User user = event.getUser();

        // Build request embed message
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder
                .setColor(Color.gray)
                .setTitle("Access Request from " + user.getName())
                .setDescription("They are applying for ``Guest`` access to this and affiliated servers.")
                .setThumbnail(user.getEffectiveAvatarUrl())
                .setFooter("User ID: " + user.getId())
                .setTimestamp(Instant.now());

        String name = null;
        String business = null;
        for (ModalMapping entry : event.getValues()) {
            // Retrieve modal information
            String id = entry.getId();
            String value = entry.getAsString();

            // Assign components of user object
            switch (id) {
                case "name" -> name = value;
                case "business" -> business = value;
            }

            // Add the entry info to the request embed
            String header = FormCommons.cypher.get(id);
            embedBuilder.addField(header, value, id.equalsIgnoreCase("note"));
        }

        // Check for component assignment errors
        if (name == null || business == null) {
            throw new RuntimeException("Failed to assign guest-access request components! One or more values are null.");
        }

        // Create a RegisteredUser to catch any data malformations
        try {
            new RegisteredUser(user.getId(), 3, name, null, business, null, null);
        } catch (RuntimeException e) {
            Commons.sendOrEdit(event, e.getMessage());
            return;
        }

        Button accept = Button.success(
                "ACCEPT_GUEST_" + user.getId() + "_" + name + "_" + business,
                "Accept as Guest"
        );
        Button deny = Button.danger(
                "DENY_" + user.getId(),
                "Deny Request"
        );

        Commons.wooshModal(event, guild, embedBuilder, accept, deny);
    }

}
