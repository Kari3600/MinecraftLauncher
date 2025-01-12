package com.kari3600.mc.launcher;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class VersionSpecification {
    private static final Gson gson = new GsonBuilder().create();

    public static VersionSpecification get(String urlString) {
        try {
            URL url = new URI(urlString).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
                System.out.println("Version JSON: ");
                String json = response.toString();
                VersionSpecification versionSpecification = gson.fromJson(json, VersionSpecification.class);
                return versionSpecification;
            } else {
                System.out.println("Failed to fetch manifest. HTTP Response Code: " + responseCode);
            }
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    class Arguments {

        public List<Object> game;
        public List<Object> jvm;


    }
    Arguments arguments;
    class Downloads {
        FileInfo client;
        FileInfo client_mappings;
        FileInfo server;
        FileInfo server_mappings;
    }
    Downloads downloads;
    class JavaVersion {
        String component;
        int majorVersion;
    }
    JavaVersion javaVersion;
    List<Library> libraries;
    String mainClass;
    String minecraftArguments;
}
