package me.coopersully.robodog.database;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.sql.*;

public class SQLiteManager {

    private static final String FILE_NAME = "robodog.db";

    private static String url;
    private static Connection conn;

    public static void createNewDatabase() {
        // Create database
        url = "jdbc:sqlite:" + FILE_NAME;
        System.out.println("Creating database in \"" + url + "\"");
        try {
            conn = DriverManager.getConnection(url);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to create " + FILE_NAME);
            System.out.println(e.getMessage());
        }
    }

    public static void connect() {
        //region General database management
        try {
            // Create a connection to the database
            DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println("Failed to establish SQLite connection.");
            System.out.println(e.getMessage());
        }
    }

    private static boolean performStatementUpdate(String sql) {
        try {
            System.out.println("Executing update \"" + sql + "\"");
            Statement statement = conn.createStatement();
            statement.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to perform statement \"" + sql + "\"");
            System.out.println(e.getMessage());
            return false;
        }
    }

    private static ResultSet performStatementQuery(String sql) {
        try {
            System.out.println("Executing query \"" + sql + "\"");
            Statement statement = conn.createStatement();
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("Failed to perform statement \"" + sql + "\"");
            throw new RuntimeException(e);
        }
    }

    public static void ensureTablesExist() {

        var guilds = performStatementUpdate("CREATE TABLE IF NOT EXISTS guilds ( id TEXT, r_unverified TEXT, r_verified TEXT, r_student TEXT, r_alumni TEXT, r_faculty TEXT, r_guest TEXT )");
        if (guilds) System.out.println("Existence of \"guilds\" table successfully confirmed.");

        /*
        id - Discord UUID
        type - Type of user (0-3)
            0 - Student
            1 - Alumni
            2 - Faculty
            3 - Guest
        name - Full Name
        email - Email including '@' symbol
        business - Association, business, or organization
        grad_year - Student's graduation year
        seen - System#currentTimeMillis() at which they were registered
        note - Additional notes, added by staff manually or Robodog automatically
         */
        var users = performStatementUpdate("CREATE TABLE IF NOT EXISTS users ( id TEXT, type INTEGER, name TEXT, email TEXT, business TEXT, grad_year INTEGER, seen INTEGER, note TEXT )");
        if (users) System.out.println("Existence of \"users\" table successfully confirmed.");
    }
    //endregion

    //region Student registration
    public static int isStudentRegistered(@NotNull User user) {
        try {
            return performStatementQuery("SELECT COUNT(*) AS total FROM users WHERE type = 0 AND id = " + user.getId())
                    .getInt("total");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    public static ResultSet getStudentByID(String id) {
        return performStatementQuery("SELECT * FROM users WHERE type = 0 AND id = '" + id + "'");
    }
    //endregion

    //region User registration
    public static int isGuestRegistered(@NotNull User user) {
        try {
            return performStatementQuery("SELECT COUNT(*) AS total FROM users WHERE type = 3 AND id = " + user.getId())
                    .getInt("total");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ResultSet getUserByID(String id) {
        return performStatementQuery("SELECT * FROM users WHERE id = '" + id + "'");
    }

    public static void registerUser(@NotNull RegisteredUser user) {
        System.out.println("Attempting to create a new user entry...");

        var command = "INSERT INTO users VALUES( '" + user.id() + "', " + user.type() + ", '" + user.name() + "', " + user.email() + ", '" + user.business() + "', '" + user.grad_year() + "', '" + System.currentTimeMillis() + "', '" + user.note() + "' )";
        performStatementUpdate(command);
    }

    @Deprecated
    public static ResultSet getGuestByID(String id) {
        return performStatementQuery("SELECT * FROM users WHERE type = 3 AND id = '" + id + "'");
    }
    //endregion

    //region Guild registration
    public static int isGuildRegistered(@NotNull Guild guild) {
        try {
            return performStatementQuery("SELECT COUNT(*) AS total FROM guilds WHERE id = " + guild.getId())
                    .getInt("total");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void registerGuild(@NotNull Guild guild) {
        System.out.println("Attempting to create a new user entry...");

        var command = "INSERT INTO guilds VALUES( '" + guild.getId() + "', NULL, NULL, NULL, NULL, NULL, NULL )";
        performStatementUpdate(command);
    }

    public static ResultSet getGuildByID(String id) {
        return performStatementQuery("SELECT * FROM guilds WHERE id = '" + id + "'");
    }

    public static void setGuildPosition(String id, String namespace, @NotNull Role role) {
        performStatementUpdate("UPDATE guilds SET r_" + namespace + " = '" + role.getId() + "' WHERE id = '" + id + "'");
    }
    //endregion

}
