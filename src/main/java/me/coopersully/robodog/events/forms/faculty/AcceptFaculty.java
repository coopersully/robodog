package me.coopersully.robodog.events.forms.faculty;

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
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;

public class AcceptFaculty extends ListenerAdapter {

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {

        if (!Commons.isButtonEventValid(event, "ACCEPT_FACULTY_")) return;

        event.deferReply().setEphemeral(true).queue();

        Guild guild = event.getGuild();
        assert guild != null;

        // Retrieve the requesting user's ID from the button
        var buttonId = event.getButton().getId();
        assert buttonId != null;

        String[] args = buttonId.split("_");
        buttonId = args[2];
        String name = args[3];
        String email = args[4];

        System.out.println("Attempting to verify user with an id of " + buttonId);
        System.out.println(">     " + Arrays.toString(args));

        /* Create a Guild Member object from the retrieved ID.
        If the object is null, the user left the guild/doesn't exist. */
        Member member;
        try {
            member = guild.retrieveMemberById(buttonId).complete();
        } catch (ErrorResponseException e) {
            Commons.sendOrEdit(event, Commons.notifFail("This user no longer exists in this server."));
            return;
        }

        // Register the user and catch any data malformations
        RegisteredUser registeredUser;
        try {
            registeredUser = new RegisteredUser(buttonId, 2, name, email, null, null, null);
        } catch (RuntimeException e) {
            event.reply(e.getMessage()).setEphemeral(true).queue();
            return;
        }
        SQLiteManager.registerUser(registeredUser);

        // Disable buttons on card and add "verified badge"
        Commons.setCardVerified(event);

        // Update user's roles
        Commons.refreshUserRoles(guild, event.getUser());

        // Notify the admin that it was successful
        Commons.sendOrEdit(event, Commons.notifSuccess("Successfully granted ``Faculty`` access to " + member.getAsMention()));

        // Alert the user that they've been verified in the server.
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder
                .setColor(Color.green)
                .setThumbnail(guild.getIconUrl())
                .setTitle("Access granted!")
                .setDescription("You've been granted faculty access in " + guild.getName() + "; if you still don't see any channels, try refreshing your Discord.")
        ;

        Commons.directMessage(member.getUser(), embedBuilder.build());
    }

}
