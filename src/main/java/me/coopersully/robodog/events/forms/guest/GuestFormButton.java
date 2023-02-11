package me.coopersully.robodog.events.forms.guest;

import me.coopersully.Commons;
import me.coopersully.robodog.database.SQLiteManager;
import me.coopersully.robodog.events.forms.FormCommons;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.jetbrains.annotations.NotNull;

public class GuestFormButton extends ListenerAdapter {
    public static final Modal verificationForm = Modal.create("verification_guest", "Verify your identity")
            .addActionRows(
                    ActionRow.of(FormCommons.name),
                    ActionRow.of(FormCommons.business),
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

        // Ensure the button is a "guest" application
        if (!buttonId.equals("GUEST")) return;

        // Ensure the user does not duplicate their registration
        event.deferReply().setEphemeral(true).queue();
        if (SQLiteManager.isUserRegistered(event.getUser())) {
            Commons.sendOrEdit(event, "**You're already registered!** You won't be able to register again. If you believe this to be an error, please contact a staff member.");
            return;
        }

        // Reply to the interaction with the modal
        event.getInteraction().replyModal(verificationForm).queue();
    }

}
