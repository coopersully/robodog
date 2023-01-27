package me.coopersully.robodog.events;

import me.coopersully.Commons;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

public class DenyUser extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {

        // If the event didn't occur in a guild, ignore it.
        if (!event.isFromGuild()) return;

        Guild guild = event.getGuild();
        assert guild != null;

        Button button = event.getButton();

        // If the button has no identifier, ignore the event.
        String buttonId = button.getId();
        if (buttonId == null) return;

        // Ensure that the event is occurring on the Deny Button
        if (!buttonId.contains("DENY_")) return;

        event.deferReply().setEphemeral(true).queue();

        // Retrieve the requesting user's ID from the button
        buttonId = buttonId.substring(5); // DENY_ has 5 characters

        /* Create a Guild Member object from the retrieved ID.
        If the object is null, the user left the guild/doesn't exist. */
        Member member = guild.getMemberById(buttonId);
        if (member == null) {
            Commons.sendOrEdit(event, ":question: It looks like that user no longer exists in this server.");
            return;
        }

        // Kick the given member, if possible.
        try {
            member.kick("Denied verification by " + event.getUser().getAsTag()).queue();
        } catch (IllegalArgumentException | InsufficientPermissionException | HierarchyException e) {
            Commons.sendOrEdit(event, ":question: I don't have permission to kick that user.");
            return;
        }

        Commons.sendOrEdit(event, ":white_check_mark: Successfully removed " + member.getAsMention() + " from the server.");

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
                        Button.of(ButtonStyle.DANGER, "DENIED", "Denied by " + event.getUser().getAsTag()).asDisabled()
                )
        ).queue();

    }

}
