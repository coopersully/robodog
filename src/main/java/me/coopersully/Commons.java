package me.coopersully;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.member.GenericGuildMemberEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

public class Commons {

    public static Random random = new Random();

    public static @Nullable Role getRoleByName(@NotNull IReplyCallback event, String name) {

        Guild guild = event.getGuild();
        assert guild != null;

        List<Role> unverifiedRoles = guild.getRolesByName(name, true);
        if (unverifiedRoles.isEmpty()) {
            sendOrEdit(event, ":question: Couldn't find a role by the name of \"" + name + "\"");
            return null;
        }
        return unverifiedRoles.get(0);
    }

    public static @Nullable Role getRoleByName(@NotNull GenericGuildMemberEvent event, String name) {
        List<Role> unverifiedRoles = event.getGuild().getRolesByName(name, true);
        if (unverifiedRoles.isEmpty()) {
            return null;
        }
        return unverifiedRoles.get(0);
    }

    public static void wooshModal(ModalInteractionEvent event, @NotNull Guild guild, @NotNull EmbedBuilder embedBuilder, Button accept, Button deny) {
        MessageEmbed messageEmbed = embedBuilder.build();

        List<TextChannel> textChannels = guild.getTextChannels();
        for (TextChannel textChannel : textChannels) {

            String topic = textChannel.getTopic();
            if (topic == null) continue;
            if (!topic.toLowerCase().contains("[requests]")) continue;

            textChannel
                    .sendMessageEmbeds(messageEmbed)
                    .setActionRows(ActionRow.of(accept, deny))
                    .queue();
        }

        sendOrEdit(event, "We've got your details; hang tight and a staff member will verify you shortly! :)");
    }

    public static void setCardVerified(@NotNull ButtonInteractionEvent event) {

        /* Edit the original message to contain only one (1) disabled
        button that displays the action taken and the actor. */

        Message message = event.getMessage();
        MessageEmbed original;
        try {
            original = message.getEmbeds().get(0);
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("A verification request contained no MessageEmbed objects.");
        }
        message.editMessageEmbeds(original).setActionRows(
                ActionRow.of(
                        Button.of(ButtonStyle.SUCCESS, "ACCEPTED", "Accepted by " + event.getUser().getAsTag()).asDisabled()
                )
        ).queue();
    }

    public static void directMessage(@NotNull User user, MessageEmbed message) {
        user.openPrivateChannel()
                .flatMap(
                        channel -> channel.sendMessageEmbeds(message)
                )
                .queue();
    }

    public static @NotNull String formatForSQL(@NotNull String string) {
        return string.replace("\"", "").replace("'", "").strip();
    }

    public static void sendOrEdit(@NotNull IReplyCallback event, String message) {
        if (event.isAcknowledged()) event.getHook().editOriginal(message).queue();
        else event.reply(message).queue();
    }

    public static void sendOrEdit(@NotNull IReplyCallback event, MessageEmbed embed) {
        if (event.isAcknowledged()) event.getHook().editOriginalEmbeds(embed).queue();
        else event.replyEmbeds(embed).queue();
    }

    public static @NotNull String blankIfNull(@Nullable String string) {
        if (string == null) return "";
        return string;
    }

    public static int getPositionByName(@NotNull String name) {
        return switch (name.toLowerCase().strip()) {
            case "student" -> 0;
            case "alumni" -> 1;
            case "faculty" -> 2;
            case "guest" -> 3;
            default -> throw new RuntimeException("An invalid position id was provided.");
        };
    }

    public static @Nullable Role getGuildRole(@NotNull ButtonInteractionEvent event, @NotNull ResultSet resultSet, String columnLabel) {
        // Retrieve the verified role and grant it to the user
        String roleId;
        Role role;
        try {
            roleId = resultSet.getString(columnLabel);
            role = event.getGuild().getRoleById(roleId);
        } catch (SQLException | NullPointerException | NumberFormatException e) {
            //throw new RuntimeException(e);
            event.reply("Failed to add one or more roles to the user; ensure that all positions are assigned.").setEphemeral(true).queue();
            return null;
        }
        return role;
    }

}
