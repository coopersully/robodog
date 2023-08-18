package me.coopersully.robodog;

import me.coopersully.robodog.database.SQLiteManager;
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

import java.awt.*;
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
            default -> throw new RuntimeException("An invalid position name was provided.");
        };
    }

    public static String getNameByPosition(int position) {
        return switch (position) {
            case 0 -> "student";
            case 1 -> "alumni";
            case 2 -> "faculty";
            case 3 -> "guest";
            default -> throw new RuntimeException("An invalid position id was provided.");
        };
    }

    public static @Nullable Role getGuildRoleByResultSet(@NotNull ButtonInteractionEvent event, @NotNull ResultSet resultSet, String columnLabel) {
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

    public static @Nullable Role getGuildRoleSilent(@NotNull Guild guild, String columnLabel) {
        // Retrieve the verified role and grant it to the user
        String roleId;
        Role role = null;
        try {
            roleId = SQLiteManager.getGuildByID(guild.getId()).getString(columnLabel);
            role = guild.getRoleById(roleId);
        } catch (SQLException | NullPointerException | NumberFormatException ignored) {
        }
        return role;
    }

    public static void refreshUserRoles(Guild guild, @NotNull User user) {
        refreshUserRoles(guild, user, true, true, true, true, true, true);
    }

    public static void refreshUserRoles(Guild guild, @NotNull User user, boolean c_unverified, boolean c_verified, boolean c_student, boolean c_alumni, boolean c_faculty, boolean c_guest) {

        Role unverified = null;
        Role verified = null;
        Role student = null;
        Role alumni = null;
        Role faculty = null;
        Role guest = null;

        if (c_unverified) unverified = SQLiteManager.getGuildUnverifiedRole(guild);
        if (c_verified) verified = SQLiteManager.getGuildVerifiedRole(guild);
        if (c_student) student = SQLiteManager.getGuildStudentRole(guild);
        if (c_alumni) alumni = SQLiteManager.getGuildAlumniRole(guild);
        if (c_faculty) faculty = SQLiteManager.getGuildFacultyRole(guild);
        if (c_guest) guest = SQLiteManager.getGuildGuestRole(guild);

        int position = -1;
        try {
            var resultSet = SQLiteManager.getUserByID(user.getId());
            position = resultSet.getInt("type");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (position == 0) {
            // If the user is a verified student
            addRoles(guild, user, verified, student);
            removeRoles(guild, user, unverified, alumni, faculty, guest);
        } else if (position == 1) {
            // If the user is a verified alumni
            addRoles(guild, user, verified, alumni);
            removeRoles(guild, user, unverified, student, faculty, guest);
        } else if (position == 2) {
            // If the user is a verified faculty member
            addRoles(guild, user, verified, faculty);
            removeRoles(guild, user, unverified, student, alumni, guest);
        } else if (position == 3) {
            // If the user is a verified guest
            addRoles(guild, user, verified, guest);
            removeRoles(guild, user, unverified, student, alumni, faculty);
        } else if (position == -1) {
            // If the user is unverified/unregistered
            addRoles(guild, user, unverified);
            removeRoles(guild, user, verified, student, alumni, faculty, guest);
        } else {
            throw new RuntimeException("Couldn't refresh roles for user with position type " + position);
        }
    }

    public static void addRoles(Guild guild, User user, Role @NotNull ... roles) {
        for (Role role : roles) {
            if (role != null) guild.addRoleToMember(user, role).queue();
        }
    }

    public static void removeRoles(Guild guild, User user, Role @NotNull ... roles) {
        for (Role role : roles) {
            if (role != null) guild.removeRoleFromMember(user, role).queue();
        }
    }

    public static boolean isButtonEventValid(@NotNull ButtonInteractionEvent event, String shouldContain) {
        // If the event didn't occur in a guild, ignore it.
        if (!event.isFromGuild()) return false;

        Guild guild = event.getGuild();
        assert guild != null;

        Button button = event.getButton();

        // If the button has no identifier, ignore the event.
        String buttonId = button.getId();
        if (buttonId == null) return false;

        // Ensure that the event is occurring on the Accept Button
        return buttonId.contains(shouldContain);
    }

    public static @NotNull MessageEmbed notifSuccess(String body) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder
                .setColor(Color.GREEN)
                .setDescription(":white_check_mark: " + body);
        return embedBuilder.build();
    }

    public static @NotNull MessageEmbed notifFail(String body) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder
                .setColor(Color.RED)
                .setDescription(":anger: " + body);
        return embedBuilder.build();
    }

}
