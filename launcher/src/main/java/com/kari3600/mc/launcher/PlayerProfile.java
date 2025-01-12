package com.kari3600.mc.launcher;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PlayerProfile {
    private static final File settings = new File("settings");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static List<PlayerProfile> profiles;
    static {
        try {
            File profilesFile = new File(settings,"profiles.json");
            if (profilesFile.createNewFile()) {
                FileWriter fileWriter = new FileWriter(profilesFile) ;
                gson.toJson(new ArrayList<PlayerProfile>(),fileWriter);
            }
            FileReader fileReader = new FileReader(profilesFile);
            profiles = gson.fromJson(fileReader, ArrayList.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String name;
    String uuid;
    String accessToken;
    public PlayerProfile(String name) {
        this.name = name;
        this.uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:"+name).getBytes()).toString();
        this.accessToken = "offline";
    }
}
