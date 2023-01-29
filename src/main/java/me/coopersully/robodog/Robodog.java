package me.coopersully.robodog;

import me.coopersully.robodog.commands.*;
import me.coopersully.robodog.database.MemberAttendances;
import me.coopersully.robodog.database.SQLiteManager;
import me.coopersully.robodog.events.DenyUser;
import me.coopersully.robodog.events.GenericButtonPressed;
import me.coopersully.robodog.events.HearLizardReplyLizard;
import me.coopersully.robodog.events.JoinGuild;
import me.coopersully.robodog.events.guest.AcceptGuest;
import me.coopersully.robodog.events.guest.GuestFormButton;
import me.coopersully.robodog.events.guest.GuestFormSend;
import me.coopersully.robodog.events.student.AcceptStudent;
import me.coopersully.robodog.events.student.StudentFormButton;
import me.coopersully.robodog.events.student.StudentFormSend;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
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
        return getJda();
    }


    public static void main(String[] args) {

        // Load config into memory
        try {
            config = new BotConfig("./config.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Create database & connect
        SQLiteManager.createNewDatabase();
        SQLiteManager.connect();
        SQLiteManager.ensureTablesExist();

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
        jda.addEventListener(new CommandProfile());
        jda.addEventListener(new CommandLookup());
        jda.addEventListener(new CommandModal());
        jda.addEventListener(new CommandIrregularities());
        jda.addEventListener(new CommandNotifyUnverified());
        jda.addEventListener(new CommandPositions());
        jda.addEventListener(new CommandSetPos());
        jda.addEventListener(new CommandMakePos());

        jda.addEventListener(new MemberAttendances());
        jda.addEventListener(new JoinGuild());

        jda.addEventListener(new GenericButtonPressed());

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
        getBranch().upsertCommand("profile", "Add or update your student profile.")
                .addOption(OptionType.STRING, "name", "Your full legal name", true)
                .addOption(OptionType.STRING, "email", "Your student email", true)
                .addOption(OptionType.INTEGER, "graduation-year", "Your expected year of graduation", true)
                .queue();
        getBranch().upsertCommand("lookup", "Look up a user's profile.")
                .addOption(OptionType.USER, "user", "The user to query", true)
                .queue();
        getBranch().upsertCommand("modal", "Send the get-verified embed with the attached modal.").queue();
        getBranch().upsertCommand("irregularities", "Find irregularities and security risks in the server.").queue();
        getBranch().upsertCommand("notify-unverified", "Notify unverified members to verify their identity.").queue();
        getBranch().upsertCommand("positions", "View all positions and their assigned roles.").queue();
        getBranch().upsertCommand("setpos", "Assign a role to a a position in the server.")
                .addSubcommands(
                        new SubcommandData("unverified", "The unverified position")
                                .addOption(OptionType.ROLE, "role", "The role to connect"),
                        new SubcommandData("verified", "The verified position")
                                .addOption(OptionType.ROLE, "role", "The role to connect"),
                        new SubcommandData("student", "The student position")
                                .addOption(OptionType.ROLE, "role", "The role to connect"),
                        new SubcommandData("alumni", "The alumni position")
                                .addOption(OptionType.ROLE, "role", "The role to connect"),
                        new SubcommandData("faculty", "The faculty position")
                                .addOption(OptionType.ROLE, "role", "The role to connect"),
                        new SubcommandData("guest", "The guest position")
                                .addOption(OptionType.ROLE, "role", "The role to connect")
                ).queue();
        getBranch().upsertCommand("makepos", "Create or assign all positions automatically.").queue();

        System.out.println("Registering all commands... Done!");

    }

}