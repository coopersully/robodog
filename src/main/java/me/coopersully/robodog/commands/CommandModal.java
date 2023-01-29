package me.coopersully.robodog.commands;

import me.coopersully.Commons;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

public class CommandModal extends ListenerAdapter {

    public static final Button STUDENT = Button.of(ButtonStyle.PRIMARY, "STUDENT", "Samford student");
    public static final Button FACULTY = Button.of(ButtonStyle.PRIMARY, "FACULTY", "Samford faculty");
    public static final Button GUEST = Button.of(ButtonStyle.SECONDARY, "GUEST", "Guest");

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (!event.getName().equals("modal")) return;
        if (!event.isFromGuild()) return;

        Guild guild = event.getGuild();
        assert guild != null;

        Member member = event.getMember();
        if (member == null) return;

        if (!member.hasPermission(Permission.ADMINISTRATOR)) {
            Commons.sendOrEdit(event, ":exclamation: You don't have permission to do that.");
            return;
        }


        TextChannel textChannel = event.getTextChannel();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder
                .setTitle("Welcome to " + guild.getName() + "!")
                .setDescription("**Before you continue, we'll need to verify your identity; to do this, click one of the buttons below. **If the buttons don't seem to work, try using the ``/verify`` command. If all else fails, feel free to ask for help by typing in this chat or direct messaging a staff member.")
                .addField("\uD83D\uDC68\u200D\uD83C\uDF93 Samford students and alumni", "If you are a current or former student of Samford University, click the button below titled, \"Samford student\"", false)
                .addField("\uD83D\uDC68\u200D\uD83C\uDF93 Samford faculty", "If you are a current faculty member at Samford University, click the button below titled, \"Samford faculty\"", false)
                .addField("\uD83D\uDC68\u200D\uD83D\uDCBC Guests", "If you have never attended Samford University, don't worry; you're still welcome here. Click the button below titled, \"Guest\"", false)
                .setThumbnail(guild.getIconUrl())
        ;

        textChannel
                .sendMessageEmbeds(embedBuilder.build())
                .setActionRows(
                        ActionRow.of(STUDENT, FACULTY, GUEST)
                )
                .queue();

        Commons.sendOrEdit(event, "Successfully created verification modal.");
        event.getHook().deleteOriginal().queue();

    }

}
