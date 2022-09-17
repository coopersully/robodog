package me.coopersully.robodog.events.student;

import me.coopersully.Commons;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.List;

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

        if (event.getMember().hasPermission(Permission.MANAGE_PERMISSIONS)) {
            event.reply(":question: You don't have permission to do that.").queue();
            return;
        }

        // Retrieve the requesting user's ID from the button
        buttonId = buttonId.substring(15); // ACCEPT_STUDENT_ has 15 characters

        /* Create a Guild Member object from the retrieved ID.
        If the object is null, the user left the guild/doesn't exist. */
        Member member = guild.getMemberById(buttonId);
        if (member == null) {
            event.reply(":question: It looks like that user no longer exists in this server.").queue();
            return;
        }

        // Get the verified role by its codename
        Role verifiedRole = Commons.getRoleByName(event, "verified");
        if (verifiedRole == null) return;

        // Get the unverified role by its codename
        Role unverifiedRole = Commons.getRoleByName(event, "quarantine");
        if (unverifiedRole == null) return;

        // Grant/revoke the given roles
        guild.addRoleToMember(member, verifiedRole).queue();
        guild.removeRoleFromMember(member, unverifiedRole).queue();

        event
                .reply(":white_check_mark: Successfully verified " + member.getAsMention())
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
