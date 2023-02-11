package me.coopersully.robodog.events.forms;

import me.coopersully.Commons;
import me.coopersully.robodog.database.SQLiteManager;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

public class FormButtons extends ListenerAdapter {
    public static final Modal guest = Modal.create("verification_guest", "Verify your identity")
            .addActionRows(
                    ActionRow.of(FormCommons.name),
                    ActionRow.of(FormCommons.business),
                    ActionRow.of(FormCommons.intentions)
            )
            .build();

    public static final Modal student = Modal.create("verification_student", "Verify your identity")
        .addActionRows(
                ActionRow.of(FormCommons.name),
                ActionRow.of(FormCommons.email),
                ActionRow.of(FormCommons.field),
                ActionRow.of(FormCommons.grad_year),
                ActionRow.of(FormCommons.intentions)
        )
        .build();

    public static final Modal faculty = Modal.create("verification_faculty", "Verify your identity")
        .addActionRows(
                ActionRow.of(FormCommons.name),
                ActionRow.of(FormCommons.email),
                ActionRow.of(FormCommons.field),
                ActionRow.of(FormCommons.intentions)
        )
        .build();

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        // Ensure the event is from a guild
        if (!event.isFromGuild()) return;

        // Ensure the button is valid
        Button button = event.getButton();
        String buttonId = button.getId();
        if (buttonId == null) return;

        // Ensure the button is a registration modal
        Modal modal;
        switch (buttonId) {
            case "STUDENT" -> modal = student;
            case "FACULTY" -> modal = faculty;
            case "GUEST" -> modal = guest;
            default -> {
                return;
            }
        }

        // Ensure the user does not duplicate their registration
        if (SQLiteManager.isUserRegistered(event.getUser())) {
            event.deferReply().setEphemeral(true).queue();
            Commons.sendOrEdit(event, Commons.notifFail("**You're already registered** and won't be able to register again. If you believe this to be an error, please contact a staff member."));
            return;
        }

        // Reply to the interaction with the modal
        event.getInteraction().replyModal(modal).queue();
    }

}
