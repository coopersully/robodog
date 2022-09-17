package me.coopersully.robodog.commands;

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

    public static final Button STUDENT = Button.of(ButtonStyle.SUCCESS, "STUDENT", "I'm a Samford student.");
    public static final Button GUEST = Button.of(ButtonStyle.PRIMARY, "GUEST", "I'm a guest.");

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (!event.getName().equals("modal")) return;
        if (!event.isFromGuild()) return;

        Guild guild = event.getGuild();
        assert guild != null;

        Member member = event.getMember();
        if (member == null) return;

        if (!member.hasPermission(Permission.ADMINISTRATOR)) {
            event.reply(":exclamation: You don't have permission to do that.").queue();
            return;
        }


        TextChannel textChannel = event.getTextChannel();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder
                .setTitle("Welcome to Community of Samford Gamers!")
                .setDescription("**Before you advance, we'll need to verify your identity; to do this, click one of the buttons below. **If the buttons don't seem to work, try using the ``/verify`` command. If all else fails, feel free to ask for help by typing in this chat or direct messaging a staff member.")
                .addField("\uD83D\uDC68\u200D\uD83C\uDF93 Samford students/alumni", "If you are a current or former student of Samford University, click the green button below titled, \"I'm a Samford student.\"", false)
                .addField("\uD83D\uDC68\u200D\uD83D\uDCBC Guests", "If you have never attended Samford University, don't fret: all are welcome. Click the gray button below titled, \"I'm a guest.\"", false)
                .setThumbnail(guild.getIconUrl())
        ;

        textChannel
                .sendMessageEmbeds(embedBuilder.build())
                .setActionRows(
                        ActionRow.of(STUDENT, GUEST)
                )
                .queue();

        event.reply("Successfully created verification modal.").setEphemeral(true).queue();

    }

}
