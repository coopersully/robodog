package me.coopersully.robodog.events.student;

import me.coopersully.Commons;
import me.coopersully.robodog.database.SQLiteManager;
import me.coopersully.robodog.database.Student;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;

public class AcceptStudent extends ListenerAdapter {

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
        if (!buttonId.contains("ACCEPT_STUDENT_")) return;

        event.deferReply().setEphemeral(true).queue();

        // Retrieve the requesting user's ID from the button
        String[] args = buttonId.split("_");
        buttonId = args[2];
        String name = args[3];
        String email = args[4];
        String year = args[5];

        Student student = new Student(buttonId, name, email, year, "");
        SQLiteManager.registerStudent(student);

        System.out.println("Attempting to verify user with an id of \"" + buttonId + "\"");
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
        String studentID;
        try {
            studentID = resultSet.getString("r_student");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (studentID == null) {
            Commons.sendOrEdit(event, ":question: A student role was never assigned; please assign one with ``/setpos student`` before accepting this user.");
            return;
        } else {
            Role studentRole = guild.getRoleById(studentID);
            if (studentRole == null) {
                Commons.sendOrEdit(event, ":question: The assigned student role no longer exists; please assign a new one with ``/setpos student`` before accepting this user.");
                return;
            }
            guild.addRoleToMember(member, studentRole).queue();
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

        Commons.sendOrEdit(event, ":white_check_mark: Successfully verified " + member.getAsMention());

        /* Alert the user that they've been verified in
        the server and ask them to join the presence page. */
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder
                .setColor(Color.green)
                .setThumbnail(guild.getIconUrl())
                .setTitle("You've been verified!")
                .setDescription("**Your identity was successfully verified in CSG; you should now be able to access all public channels and chat with other members.**")
        ;

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
                .appendDescription("\n\u2022 [@samfordgamers on Instagram](https://instagram.com/samfordgamers)")
                .appendDescription("\n\u2022 [@samfordgamers on Twitter](https://twitter.com/samfordgamers)")
                .appendDescription("\n\u2022 [@samfordgamers on Facebook](https://www.facebook.com/groups/samfordgamers)")
                .appendDescription("\n\u2022 [@samfordgamers on Twitch](https://www.twitch.tv/samfordgamers)")
        ;

        Commons.directMessage(memberUser, embedBuilder.build());
    }

}
