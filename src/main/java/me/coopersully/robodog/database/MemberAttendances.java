package me.coopersully.robodog.database;

import me.coopersully.Commons;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MemberAttendances extends ListenerAdapter {

    // When a user joins the server
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {

        Guild guild = event.getGuild();
        User user = event.getUser();

        int isGuest = SQLiteManager.isGuestRegistered(user);
        if (isGuest > 0) {
            Role role = Commons.getRoleByName(event, "guest");
            if (role == null) {
                throw new RuntimeException("Failed to automatically add guest role to " + user.getAsTag() + "; one didn't exist.");
            }
            addRoleToMember(guild, role, user);
        }

        int isStudent = SQLiteManager.isStudentRegistered(user);
        if (isStudent > 0) {
            Role role = Commons.getRoleByName(event, "verified");
            if (role == null) {
                throw new RuntimeException("Failed to automatically add student role to " + user.getAsTag() + "; one didn't exist.");
            }
            addRoleToMember(guild, role, user);
        }

    }

    private void addRoleToMember(@NotNull Guild guild, Role role, User user) {
        try {
            guild.addRoleToMember(user, role).queue();
        } catch (HierarchyException | InsufficientPermissionException | IllegalArgumentException e) {
            System.out.println("Failed to add @" + role.getName() + " role to " + user.getAsTag() + " in " + guild.getName() + ".");
            if (e instanceof HierarchyException) {
                System.out.println("The provided role is higher in the hierarchy than Robodog and thus cannot be modified.");
                return;
            }
            if (e instanceof InsufficientPermissionException) {
                System.out.println("Robodog doesn't have the MANAGE_ROLES permission in this server.");
                return;
            }
            System.out.println(e.getMessage());
        }
    }

}