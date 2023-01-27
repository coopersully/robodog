package me.coopersully.robodog.events.guest;

import me.coopersully.Commons;
import me.coopersully.robodog.database.Guest;
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
import java.sql.SQLException;
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

        Guest guest = new Guest(buttonId, name, Commons.blankIfNull(business), "");
        SQLiteManager.registerGuest(guest);

        System.out.println("Attempting to verify user with an id of " + buttonId);
        System.out.println(">     " + Arrays.toString(args));

        // Disable buttons on card and add "verified badge"
        Commons.setCardVerified(event);

        /* Create a Guild Member object from the retrieved ID.
        If the object is null, the user left the guild/doesn't exist. */
        Member member;
        try {
            member = guild.retrieveMemberById(buttonId).complete();
        } catch (ErrorResponseException e) {
            Commons.sendOrEdit(event, ":question: It looks like that user no longer exists in this server. However, their record has been created in the database and they will be automatically verified if they re-join.");
            return;
        }

        // Get server roles
        var resultSet = SQLiteManager.getGuildByID(guild.getId());

        // Retrieve the verified role and grant it to the user
        String verifiedID;
        try {
            verifiedID = resultSet.getString("r_verified");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (verifiedID != null) {
            Role verifiedRole = guild.getRoleById(verifiedID);
            if (verifiedRole != null) guild.addRoleToMember(member, verifiedRole).queue();
        }

        // Retrieve the guest role and grant it to the user
        String guestID;
        try {
           guestID = resultSet.getString("r_guest");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (guestID == null) {
            Commons.sendOrEdit(event, ":question: A guest role was never assigned; please assign one with ``/setpos guest`` before accepting this user.");
            return;
        } else {
            Role guestRole = guild.getRoleById(guestID);
            if (guestRole == null) {
                Commons.sendOrEdit(event, ":question: The assigned guest role no longer exists; please assign a new one with ``/setpos guest`` before accepting this user.");
                return;
            }
            guild.addRoleToMember(member, guestRole).queue();
        }

        // Retrieve the unverified role and revoke it from the user
        String unverifiedID;
        try {
            unverifiedID = resultSet.getString("r_unverified");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (unverifiedID != null) {
            Role unverifiedRole = guild.getRoleById(unverifiedID);
            if (unverifiedRole != null) guild.removeRoleFromMember(member, unverifiedRole).queue();
        }

        // Notify the admin that it was successful
        Commons.sendOrEdit(event, ":white_check_mark: Successfully granted access to " + member.getAsMention());

        /* Alert the user that they've been verified in
        the server and ask them to join the presence page. */
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder
                .setColor(Color.green)
                .setThumbnail(guild.getIconUrl())
                .setTitle("Access granted!")
                .setDescription("You've been granted guest access in " + guild.getName() + "; you should now be able to access a few of the server's chat and voice channels.")
        ;

        Commons.directMessage(member.getUser(), embedBuilder.build());
    }

}
