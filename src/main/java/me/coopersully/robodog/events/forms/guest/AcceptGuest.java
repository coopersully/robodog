package me.coopersully.robodog.events.forms.guest;

import me.coopersully.Commons;
import me.coopersully.robodog.database.RegisteredUser;
import me.coopersully.robodog.database.SQLiteManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.Arrays;

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

        event.deferReply().setEphemeral(true).queue();

        // Retrieve the requesting user's ID from the button
        String[] args = buttonId.split("_");
        buttonId = args[2];
        String name = args[3];
        String business = null;
        if (args.length >= 5) business = args[4];

        System.out.println("Attempting to verify user with an id of " + buttonId);
        System.out.println(">     " + Arrays.toString(args));

        /* Create a Guild Member object from the retrieved ID.
        If the object is null, the user left the guild/doesn't exist. */
        Member member;
        try {
            member = guild.retrieveMemberById(buttonId).complete();
        } catch (ErrorResponseException e) {
            Commons.sendOrEdit(event, ":question: That user no longer exists in this server.");
            return;
        }

        // Register the user
        RegisteredUser guest =
                new RegisteredUser(buttonId, 3, name, null, business, null, null);
        SQLiteManager.registerUser(guest);

        // Disable buttons on card and add "verified badge"
        Commons.setCardVerified(event);

        // Get server roles
        var resultSet = SQLiteManager.getGuildByID(guild.getId());

        // Retrieve the related role(s) role and update the user
        Role unverifiedRole = Commons.getGuildRole(event, resultSet, "r_unverified");
        if (unverifiedRole == null) return; // If null, event is replied to above.
        guild.removeRoleFromMember(member, unverifiedRole).queue();

        Role verifiedRole = Commons.getGuildRole(event, resultSet, "r_verified");
        if (verifiedRole == null) return; // If null, event is replied to above.
        guild.addRoleToMember(member, verifiedRole).queue();

        Role guestRole = Commons.getGuildRole(event, resultSet, "r_guest");
        if (guestRole == null) return; // If null, event is replied to above.
        guild.addRoleToMember(member, guestRole).queue();

        // Notify the admin that it was successful
        Commons.sendOrEdit(event, ":white_check_mark: Successfully granted access to " + member.getAsMention());

        // Alert the user that they've been verified in the server.
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder
                .setColor(Color.green)
                .setThumbnail(guild.getIconUrl())
                .setTitle("Access granted!")
                .setDescription("You've been granted guest access in " + guild.getName() + "; if you still don't see any channels, try refreshing your Discord.")
        ;

        Commons.directMessage(member.getUser(), embedBuilder.build());
    }

}
