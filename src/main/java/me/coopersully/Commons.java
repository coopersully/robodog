package me.coopersully;

import me.coopersully.robodog.Robodog;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class Commons {

    public static Random random = new Random();
    private static final Guild guild = Robodog.getJda().getGuildById(Robodog.getConfig().guildID);

    public static @Nullable Role getRoleByName(ButtonInteractionEvent event, String name) {
        assert guild != null;
        List<Role> unverifiedRoles = guild.getRolesByName(name, true);
        if (unverifiedRoles.isEmpty()) {
            event.reply(":question: Couldn't find a role by the name of \"" + name + "\"").queue();
            return null;
        }
        return unverifiedRoles.get(0);
    }

    public static void wooshModal(ModalInteractionEvent event, Guild guild, EmbedBuilder embedBuilder, Button accept, Button deny) {
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

        event
                .reply("We've got your details; hang tight and a staff member will verify you shortly! :)")
                .setEphemeral(true)
                .queue();
    }

    public static void setCardVerified(ButtonInteractionEvent event) {

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

}
