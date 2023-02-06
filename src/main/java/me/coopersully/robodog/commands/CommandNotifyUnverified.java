package me.coopersully.robodog.commands;

import me.coopersully.Commons;
import me.coopersully.robodog.database.SQLiteManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

public class CommandNotifyUnverified extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        var command = event.getCommandPath();
        if (!command.contains("notify-unverified")) return;

        Guild guild = event.getGuild();
        if (guild == null) return;

        if (!event.getMember().hasPermission(Permission.MANAGE_PERMISSIONS)) {
            Commons.sendOrEdit(event, ":question: You don't have permission to do that.");
            return;
        }

        event.deferReply().setEphemeral(true).queue();

        Role unverified = SQLiteManager.getGuildUnverifiedRole(guild);
        if (unverified == null) {
            Commons.sendOrEdit(event, "There is not an unverified role assigned in this guild.");
            return;
        }

        guild.findMembersWithRoles(unverified).onSuccess((members -> notifyMembers(members, event)));
        Commons.sendOrEdit(event, "No unverified members were found.");
    }

    private void notifyMembers(@NotNull List<Member> members, SlashCommandInteractionEvent event) {

        if (members.isEmpty()) return;

        Guild guild = event.getGuild();
        assert guild != null;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder
                .setColor(Color.CYAN)
                .setTitle("Be sure to verify yourself in " + guild.getName() + "!")
                .setDescription("It looks like you haven't verified your identity in " + guild.getName() + " quite yet. Be sure to do this soon to be able to access the server's chat and voice channels.");

        for (Member member : members) {
            System.out.println("Notifying " + member.getEffectiveName() + " to verify themselves in " + guild.getName());
            Commons.directMessage(member.getUser(), embedBuilder.build());
            Commons.sendOrEdit(event, "Successfully notified **" + members.size() + "** unverified members.");
        }
    }

}
