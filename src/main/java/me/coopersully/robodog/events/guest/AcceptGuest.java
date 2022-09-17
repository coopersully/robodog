package me.coopersully.robodog.events.guest;

import me.coopersully.Commons;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;

public class AcceptGuest extends ListenerAdapter {

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

        // Ensure that the event is occurring on the Accept Button
        if (!buttonId.contains("ACCEPT_GUEST_")) return;

        // Retrieve the requesting user's ID from the button
        buttonId = buttonId.substring(13); // ACCEPT_GUEST_ has 13 characters

        /* Create a Guild Member object from the retrieved ID.
        If the object is null, the user left the guild/doesn't exist. */
        Member member = guild.getMemberById(buttonId);
        if (member == null) {
            event.reply(":question: It looks like that user no longer exists in this server.").queue();
            return;
        }

        // Get the guest role by its codename
        Role guestRole = Commons.getRoleByName(event, "guest");
        if (guestRole == null) return;

        // Get the unverified role by its codename
        Role unverifiedRole = Commons.getRoleByName(event, "quarantine");
        if (unverifiedRole == null) return;

        // Grant/revoke the given roles
        guild.addRoleToMember(member, guestRole).queue();
        guild.removeRoleFromMember(member, unverifiedRole).queue();

        event
                .reply(":white_check_mark: Successfully granted access to " + member.getAsMention())
                .setEphemeral(true)
                .queue();

        // Disable buttons on card and add "verified badge"
        Commons.setCardVerified(event);

        /* Alert the user that they've been verified in
        the server and ask them to join the presence page. */
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder
                .setColor(Color.green)
                .setThumbnail(guild.getIconUrl())
                .setTitle("Access granted!")
                .setDescription("You've been granted guest access in CSG; you should now be able to access a few of the server's chat and voice channels.")
        ;

        Commons.directMessage(member.getUser(), embedBuilder.build());
    }

}
