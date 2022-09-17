package me.coopersully.robodog;

import me.coopersully.robodog.commands.CommandModal;
import me.coopersully.robodog.commands.CommandVerify;
import me.coopersully.robodog.events.HearLizardReplyLizard;
import me.coopersully.robodog.events.guest.AcceptGuest;
import me.coopersully.robodog.events.guest.GuestFormButton;
import me.coopersully.robodog.events.guest.GuestFormSend;
import me.coopersully.robodog.events.student.AcceptStudent;
import me.coopersully.robodog.events.DenyUser;
import me.coopersully.robodog.events.student.StudentFormButton;
import me.coopersully.robodog.events.student.StudentFormSend;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class Robodog {

    private static JDA jda;
    private static BotConfig config;

    public static JDA getJda() {
        return jda;
    }

    public static BotConfig getConfig() {
        return config;
    }

    public static @NotNull JDA getBranch() {
        Guild guild = jda.getGuildById(config.guildID);
        if (guild == null) {
            throw new RuntimeException("Guild could not be found; contact the developer.");
        }
        return guild.getJDA();
    }


    public static void main(String[] args) {

        // Load config into memory
        try {
            config = new BotConfig("./config.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /* Login with the given application token and
        enable intents, set cache policies, etc. */
        try {
            jda = JDABuilder
                    .createDefault(config.token)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .build();
        } catch (LoginException e) {
            System.out.println("Failed to log-in using the bot-token in config.yml. Please check the token and try again.");
            return;
        }

        // Set cosmetic status
        jda.getPresence().setStatus(OnlineStatus.ONLINE);
        jda.getPresence().setActivity(Activity.watching("CSG"));

        // Register all event listeners
        jda.addEventListener(new CommandVerify());
        jda.addEventListener(new CommandModal());

        jda.addEventListener(new StudentFormButton());
        jda.addEventListener(new StudentFormSend());
        jda.addEventListener(new AcceptStudent());

        jda.addEventListener(new GuestFormButton());
        jda.addEventListener(new GuestFormSend());
        jda.addEventListener(new AcceptGuest());

        jda.addEventListener(new DenyUser());

        jda.addEventListener(new HearLizardReplyLizard());


        // Wait for the bot to be ready
        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Register all commands
        System.out.println("Registering all commands...");

        getBranch().updateCommands().queue();
        getBranch().upsertCommand("verify", "Verify your identity!").queue();
        getBranch().upsertCommand("modal", "Send the get-verified embed with the attached modal.").queue();

        System.out.println("Registering all commands... Done!");

    }

}