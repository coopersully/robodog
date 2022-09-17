package me.coopersully.robodog;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BotConfig {

    // Content management
    public final String content;
    public final JSONObject jsonObject;

    // Options
    public final String token;
    public final String guildID;

    public BotConfig(String path) throws IOException {

        // Content management
        this.content = Files.readString(Path.of(path));
        this.jsonObject = new JSONObject(content);

        // Options
        this.token = jsonObject.getString("token");
        this.guildID = jsonObject.getString("guild");
    }

}
