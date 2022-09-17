package me.coopersully.robodog.commands;

import me.coopersully.robodog.events.guest.GuestFormButton;
import me.coopersully.robodog.events.student.StudentFormButton;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandVerify extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        var command = event.getCommandPath();
        if (!command.contains("verify")) return;

        if (command.contains("guest")) {
            event.replyModal(GuestFormButton.verificationForm).queue();
            return;
        }

        event.replyModal(StudentFormButton.verificationForm).queue();
    }

}
