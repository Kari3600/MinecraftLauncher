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
import com.google.gson.reflect.TypeToken;

public class PlayerProfile {
    private static final File settings = new File("settings");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static List<PlayerProfile> profiles = new ArrayList<>(); // Initialize as empty list
    
    // Static block to initialize profiles from the profiles.json file
    static {
        File profilesFile = new File(settings, "profiles.json");
        try {
            if (!profilesFile.exists()) {
                // If the file doesn't exist, create an empty profiles file
                if (profilesFile.createNewFile()) {
                    try (FileWriter fileWriter = new FileWriter(profilesFile)) {
                        gson.toJson(new ArrayList<PlayerProfile>(), fileWriter);
                    }
                }
            }
            
            try (FileReader fileReader = new FileReader(profilesFile)) {
                // Use TypeToken to ensure the correct deserialization of the List<PlayerProfile>
                profiles = gson.fromJson(fileReader, new TypeToken<List<PlayerProfile>>() {}.getType());
                if (profiles == null) {
                    profiles = new ArrayList<>();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String name;
    String uuid;
    String accessToken;

    // Constructor for creating a new player profile
    public PlayerProfile(String name) {
        this.name = name;
        this.uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes()).toString();
        this.accessToken = "offline";
    }

    public String getName() {
        return name;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
