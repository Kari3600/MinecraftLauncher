package com.kari3600.mc.launcher;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class VersionList {
    private static final String manifestAddress = "https://piston-meta.mojang.com/mc/game/version_manifest.json";
    private static final Gson gson = new GsonBuilder().create();
    private static VersionList instance;
    static {
        System.out.println("Creating new VersionList");
        try {
            URL manifestURL = new URI(manifestAddress).toURL();
            HttpURLConnection connection = (HttpURLConnection) manifestURL.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                System.out.println("Manifest JSON: ");
                
                String json = response.toString();
                instance = gson.fromJson(json, VersionList.class);
                for (Version version : instance.versions) {
                    System.out.println(version.id+" - "+version.type);
                }
                System.out.println(instance.versions.size());
            } else {
                throw new Exception("Failed to fetch manifest. HTTP Response Code: " + responseCode);
            }
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static VersionList getInstance() {
        return instance;
    }

    List<Version> versions;
}
