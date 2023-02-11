package me.coopersully.robodog.events.forms.student;

import me.coopersully.Commons;
import me.coopersully.robodog.Robodog;
import me.coopersully.robodog.database.RegisteredUser;
import me.coopersully.robodog.database.SQLiteManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.LocalDate;
import java.util.Arrays;

public class AcceptStudent extends ListenerAdapter {

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {

        if (!Commons.isButtonEventValid(event, "ACCEPT_STUDENT_")) return;

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
        String year = args[5];

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

        // Determine whether the user is a student or alumni
        int type = 0;
        LocalDate now = LocalDate.now();
        if (now.getYear() > Integer.parseInt(year)) {
            type = 1; // Alumni
        }

        // Register the user and catch any data malformations
        RegisteredUser registeredUser;
        try {
            registeredUser = new RegisteredUser(buttonId, type, name, email, null, year, null);
        } catch (RuntimeException e) {
            event.reply(e.getMessage()).setEphemeral(true).queue();
            return;
        }
        SQLiteManager.registerUser(registeredUser);

        // Disable buttons on card and add "verified badge"
        Commons.setCardVerified(event);

        // Update user's roles
        Commons.refreshUserRoles(guild, event.getUser());

        Commons.sendOrEdit(event, Commons.notifSuccess("Successfully granted ``Student`` access to " + member.getAsMention()));

        // Alert the user that they've been verified inthe server
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder
                .setColor(Color.green)
                .setThumbnail(guild.getIconUrl())
                .setTitle("You've been verified!")
                .setDescription("**Your identity was successfully verified in " + guild.getName() + "; you should now be able to access all public channels and chat with other members.**")
        ;

        // If the server is CSG, send server-specific information
        if (!Robodog.getConfig().guildID.equals(guild.getId())) return;

        User memberUser = member.getUser();
        Commons.directMessage(memberUser, embedBuilder.build());

        embedBuilder
                .setThumbnail(null)
                .setTitle("Become an official member!", "https://samford.presence.io/organization/community-of-samford-gamers-csg")
                .setDescription("Make sure you join our Bulldog Central page on Samford's website to be considered an official member of the club. This allows us to get increased funding for game nights, d&d sessions, esports teams, and more!")
                .appendDescription("\n[Click here to become a club member.](https://samford.presence.io/organization/community-of-samford-gamers-csg)")
        ;

        Commons.directMessage(memberUser, embedBuilder.build());

        embedBuilder
                .setTitle("Get connected with the community!", "https://linktr.ee/csgsamford")
                .setDescription("We'd love for you to join us for any and all of our upcoming events! Follow us on social media to stay up-to-date on all of our announcements and events.")
                .appendDescription("\n")
                .appendDescription("\n• [@samfordgamers on Instagram](https://instagram.com/samfordgamers)")
                .appendDescription("\n• [@samfordgamers on Twitter](https://twitter.com/samfordgamers)")
                .appendDescription("\n• [@samfordgamers on Facebook](https://www.facebook.com/groups/samfordgamers)")
                .appendDescription("\n• [@samfordgamers on Twitch](https://www.twitch.tv/samfordgamers)")
        ;

        Commons.directMessage(memberUser, embedBuilder.build());
    }

}
