package me.coopersully.robodog.commands;

import me.coopersully.Commons;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public class CommandIrregularities extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        var command = event.getCommandPath();
        if (!command.contains("irregularities")) return;

        Guild guild = event.getGuild();
        if (guild == null) return;

        if (!event.getMember().hasPermission(Permission.MANAGE_PERMISSIONS)) {
            Commons.sendOrEdit(event, ":question: You don't have permission to do that.");
            return;
        }

        event.deferReply().setEphemeral(true).queue();

        guild.loadMembers().onSuccess(
                (members -> goThroughList(members, event))
        );
    }

    private static void goThroughList(List<Member> members, SlashCommandInteractionEvent event) {

        Guild guild = event.getGuild();
        assert guild != null;

        Role verified = Commons.getRoleByName(event, "verified");
        Role unverified = Commons.getRoleByName(event, "quarantine");
        Role guest = Commons.getRoleByName(event, "guest");

        List<Member> irregularities = new ArrayList<>();
        for (Member member : members) {
            List<Role> roles = member.getRoles();
            if (roles.contains(verified)) {
                if (roles.contains(unverified)) irregularities.add(member);
                if (roles.contains(guest)) irregularities.add(member);
            }
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(guild.getName() + "'s irregularities");

        if (members.isEmpty()) {
            embedBuilder.setDescription("No irregularities found.");
        } else {
            for (Member member : irregularities) {
                embedBuilder.appendDescription("\n" + member.getAsMention());
            }
        }

        event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();

    }

}
