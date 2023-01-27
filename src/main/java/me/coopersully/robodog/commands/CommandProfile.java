package me.coopersully.robodog.commands;

import me.coopersully.Commons;
import me.coopersully.robodog.database.SQLiteManager;
import me.coopersully.robodog.database.Student;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.awt.*;

public class CommandProfile extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        var command = event.getCommandPath();
        if (!command.contains("profile")) return;

        event.deferReply().setEphemeral(true).queue();

        User user = event.getUser();
        System.out.println(user.getAsTag() + " is attempting to update their profile.");

        // Ensure name is valid
        OptionMapping mapName = event.getOption("name");
        if (mapName == null) {
            Commons.sendOrEdit(event, "Please include all fields in your command; you didn't include the ``name`` field.");
            return;
        }
        String name = mapName.getAsString();
        if (!name.contains(" ")) {
            Commons.sendOrEdit(event, "Something went wrong; please provide your *full name* for your profile.");
            return;
        }

        // Ensure email is valid
        OptionMapping mapEmail = event.getOption("email");
        if (mapEmail == null) {
            Commons.sendOrEdit(event, "Please include all fields in your command; you didn't include the ``email`` field.");
            return;
        }
        String email = mapEmail.getAsString().toLowerCase();
        if (!email.contains("@samford.edu")) {
            Commons.sendOrEdit(event, "It looks like that's not a valid student email; please try again with your Samford email address.");
            return;
        }

        // Ensure year is valid
        OptionMapping mapYear = event.getOption("graduation-year");
        if (mapYear == null) {
            Commons.sendOrEdit(event, "Please include all fields in your command; you didn't include the ``email`` field.");
            return;
        }
        String year = mapYear.getAsString().toLowerCase();

        if (year.length() == 2) year = "20" + year;
        if (year.length() != 4) {
            Commons.sendOrEdit(event, "Please include all fields in your command; you didn't include the ``year`` field.");
            return;
        }
        if (!year.startsWith("20")) {
            Commons.sendOrEdit(event, "The graduation year provided doesn't look valid, try providing a 4-digit year.");
            return;
        }

        // Ensure student isn't already registered
        int isRegistered = SQLiteManager.isStudentRegistered(user);
        if (isRegistered > 0) {
            Commons.sendOrEdit(event, "You're already registered! If you'd like to change your details, please contact an admin.");
            return;
        }

        // Register the student
        Student student =
                new Student(user.getId(), name, email, year,"Registered manually via /profile");
        SQLiteManager.registerStudent(student);

        // Alert the student
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder
                .setColor(Color.green)
                .setTitle("Thanks for your help, " + user.getName() + "!")
                .setDescription("We've got you all registered. Before you're done, please be sure that you've joined our Bulldog Central page on Samford's website to be considered an official member of the club. This allows us to get increased funding for game nights, d&d sessions, esports teams, and more! If you're not already a club member and want to join, you can visit this link: https://samford.presence.io/organization/community-of-samford-gamers-csg");

        Commons.sendOrEdit(event, embedBuilder.build());
        System.out.println(user.getAsTag() + " is attempting to update their profile. Success!");
    }

}
