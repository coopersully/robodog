package me.coopersully.robodog.events.student;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

public class StudentFormButton extends ListenerAdapter {

    private static final TextInput name = TextInput.create("name", "Full Name", TextInputStyle.SHORT)
            .setPlaceholder("i.e. John Appleseed")
            .setMaxLength(100)
            .build();

    private static final TextInput major = TextInput.create("major", "Major", TextInputStyle.SHORT)
            .setPlaceholder("i.e. Business")
            .setRequiredRange(1, 33)
            .build();

    private static final TextInput email = TextInput.create("email", "School Email", TextInputStyle.SHORT)
            .setPlaceholder("i.e. jappleseed@samford.edu")
            .setRequiredRange(13, 33)
            .build();

    private static final TextInput games = TextInput.create("games", "What games do you frequently play?", TextInputStyle.PARAGRAPH)
            .setPlaceholder("We'd love to get you connected with other members who play similar games.")
            .setRequired(false)
            .setMaxLength(1000)
            .build();

    public static final Modal verificationForm = Modal.create("verification_student", "Verify your identity")
            .addActionRows(
                    ActionRow.of(name),
                    ActionRow.of(major),
                    ActionRow.of(email),
                    ActionRow.of(games)
            )
            .build();

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {

        if (!event.isFromGuild()) return;

        Button button = event.getButton();

        String buttonId = button.getId();
        if (buttonId == null) return;

        if (!buttonId.equals("STUDENT")) return;

        event.getInteraction().replyModal(verificationForm).queue();
    }

}
