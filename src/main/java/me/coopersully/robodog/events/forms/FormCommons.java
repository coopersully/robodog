package me.coopersully.robodog.events.forms;

import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

import java.util.Map;

public class FormCommons {

    public static final TextInput name = TextInput.create("name", "Full Name", TextInputStyle.SHORT)
            .setPlaceholder("i.e. John Appleseed")
            .setMaxLength(100)
            .build();

    public static final TextInput field = TextInput.create("field", "Field of Study", TextInputStyle.SHORT)
            .setPlaceholder("i.e. Business")
            .setRequiredRange(1, 33)
            .build();

    public static final TextInput email = TextInput.create("email", "School Email", TextInputStyle.SHORT)
            .setPlaceholder("i.e. jappleseed@samford.edu")
            .setRequiredRange(13, 33)
            .build();

    public static final TextInput business = TextInput.create("business", "Business/organization", TextInputStyle.SHORT)
            .setPlaceholder("Leave this blank if it's not applicable.")
            .setRequired(false)
            .setMaxLength(100)
            .build();

    public static final TextInput grad_year = TextInput.create("grad_year", "Graduation Year", TextInputStyle.SHORT)
            .setPlaceholder("i.e. 2020")
            .setRequiredRange(4, 4)
            .build();

    public static final TextInput intentions = TextInput.create("intentions", "Tell us more about you.", TextInputStyle.PARAGRAPH)
            .setPlaceholder("Let us know any interests in specific games or communities to help our staff get you connected.")
            .setRequired(false)
            .setMaxLength(1000)
            .build();

    public static final Map<String, String> cypher = Map.ofEntries(
            Map.entry("id", "User ID"),
            Map.entry(name.getId(), name.getLabel()),
            Map.entry(field.getId(), field.getLabel()),
            Map.entry(email.getId(), email.getLabel()),
            Map.entry(business.getId(), business.getLabel()),
            Map.entry(grad_year.getId(), grad_year.getLabel()),
            Map.entry(intentions.getId(), intentions.getLabel())
    );

}
