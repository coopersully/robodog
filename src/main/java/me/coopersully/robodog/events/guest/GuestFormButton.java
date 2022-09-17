package me.coopersully.robodog.events.guest;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

public class GuestFormButton extends ListenerAdapter {

    private static final TextInput name = TextInput.create("name", "Full Name", TextInputStyle.SHORT)
            .setPlaceholder("i.e. John Appleseed")
            .setMaxLength(100)
            .build();

    private static final TextInput org = TextInput.create("org", "Organization/business", TextInputStyle.SHORT)
            .setPlaceholder("Leave this blank if it's not applicable.")
            .setRequired(false)
            .setMaxLength(100)
            .build();

    private static final TextInput purpose = TextInput.create("purpose", "What's the purpose of your visit?", TextInputStyle.PARAGRAPH)
            .setPlaceholder("This will help our staff best connect you with students or server members.")
            .setRequired(false)
            .setMaxLength(1000)
            .build();

    public static final Modal verificationForm = Modal.create("verification_guest", "Verify your identity")
            .addActionRows(
                    ActionRow.of(name),
                    ActionRow.of(org),
                    ActionRow.of(purpose)
            )
            .build();

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {

        if (!event.isFromGuild()) return;

        Button button = event.getButton();

        String buttonId = button.getId();
        if (buttonId == null) return;

        if (!buttonId.equals("GUEST")) return;

        event.getInteraction().replyModal(verificationForm).queue();
    }

}
