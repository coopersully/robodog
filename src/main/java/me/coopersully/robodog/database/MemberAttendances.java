package me.coopersully.robodog.database;

import me.coopersully.Commons;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MemberAttendances extends ListenerAdapter {

    /* When a user joins the server, add their roles if they have any
    according to their registered positions. If not, add the unverified
    role to their user. */
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {

        Guild guild = event.getGuild();
        User user = event.getUser();

        Commons.refreshUserRoles(guild, user);
    }
}