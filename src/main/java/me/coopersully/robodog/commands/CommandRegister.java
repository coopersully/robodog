package me.coopersully.robodog.commands;

import me.coopersully.Commons;
import me.coopersully.robodog.database.RegisteredUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class CommandRegister extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        var command = event.getCommandPath();
        if (!command.contains("register")) return;

        // Decipher user position integer
        int type;
        var subcommand = event.getSubcommandName();
        try {
            assert subcommand != null;
            type = Commons.getPositionByName(subcommand);
        } catch (RuntimeException e) {
            Commons.sendOrEdit(event, e.getMessage());
            return;
        }

        // Retrieve name if it is valid
        OptionMapping mapName = event.getOption("user");
        if (mapName == null) {
            Commons.sendOrEdit(event, "Please provide your full name.");
            return;
        }
        String name = mapName.getAsString();

        // Retrieve email if it is valid
        String email = null;
        OptionMapping mapEmail = event.getOption("email");
        if (mapEmail != null) {
            email = mapEmail.getAsString();
        }

        // Retrieve business if it is valid
        String business = null;
        OptionMapping mapBusiness = event.getOption("business");
        if (mapBusiness != null) {
            business = mapBusiness.getAsString();
        }

        // Retrieve grad_year if it is valid
        String grad_year = null;
        OptionMapping mapGradYear = event.getOption("grad_year");
        if (mapGradYear != null) {
            grad_year = mapGradYear.getAsString();
        }

        User user = event.getUser();
        RegisteredUser registeredUser;
        try {
            registeredUser = new RegisteredUser(
                    user.getId(),
                    type,
                    name,
                    email,
                    business,
                    grad_year,
                    "Registered via /register"
            );
        } catch (RuntimeException e) {
            Commons.sendOrEdit(event, e.getMessage());
        }

        event.deferReply().setEphemeral(true).queue();
    }

}
