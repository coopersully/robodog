package me.coopersully.robodog.events;

import me.coopersully.robodog.database.SQLiteManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class JoinGuild extends ListenerAdapter {

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        Guild guild = event.getGuild();
        if (SQLiteManager.isGuildRegistered(guild) > 0) return;
        SQLiteManager.registerGuild(guild);
    }


}
