package me.coopersully.robodog.events;

import me.coopersully.robodog.database.SQLiteManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class Ready extends ListenerAdapter {

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        Guild guild = event.getGuild();
        if (SQLiteManager.isGuildRegistered(guild)) return;
        SQLiteManager.registerGuild(guild);
    }

}
