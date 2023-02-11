package me.coopersully.robodog.commands;

import me.coopersully.Commons;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandFixPos extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        var command = event.getCommandPath();
        if (!command.contains("fixpos")) return;

        Guild guild = event.getGuild();
        if (guild == null) return;

        if (!event.getMember().hasPermission(Permission.MANAGE_PERMISSIONS)) {
            Commons.sendOrEdit(event, Commons.notifFail("You don't have permission to do that."));
            return;
        }

        event.deferReply().setEphemeral(true).queue();

        boolean c_unverified = getOptionAsBoolean("unverified", event);
        boolean c_verified = getOptionAsBoolean("verified", event);
        boolean c_student = getOptionAsBoolean("student", event);
        boolean c_alumni = getOptionAsBoolean("alumni", event);
        boolean c_faculty = getOptionAsBoolean("faculty", event);
        boolean c_guest = getOptionAsBoolean("guest", event);

        guild.loadMembers().onSuccess(
                (members -> fix(members, event, c_unverified, c_verified, c_student, c_alumni, c_faculty, c_guest))
        );
    }

    private boolean getOptionAsBoolean(String optionName, @NotNull SlashCommandInteractionEvent event) {
        OptionMapping optionMap = event.getOption(optionName);
        if (optionMap == null) return false;
        return optionMap.getAsBoolean();
    }

    private static void fix(@NotNull List<Member> members, @NotNull SlashCommandInteractionEvent event, boolean c_unverified, boolean c_verified, boolean c_student, boolean c_alumni, boolean c_faculty, boolean c_guest) {

        Guild guild = event.getGuild();
        assert guild != null;

        for (var member : members) {
            Commons.refreshUserRoles(guild, member.getUser(), c_unverified, c_verified, c_student, c_alumni, c_faculty, c_guest);
        }

        Commons.sendOrEdit(event, Commons.notifSuccess("Successfully fixed role assignments for the given positions."));
    }

}
