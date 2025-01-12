package com.kari3600.mc.launcher;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;

public class LauncherOptions {
    private static final File directory = new File(".");
    private static final File libraries = new File(directory,"libraries");
    private static final File assets = new File("assets");
    private static final File natives = new File("natives");
    private static final File versions = new File("versions");
    private static final File logs = new File("logs");
    private static final File settings = new File("settings");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static LauncherOptions instance;

    static {
        try {
            File optionsFile = new File(settings,"launcher_options.json");
            if (optionsFile.createNewFile()) {
                instance = new LauncherOptions();
                instance.synchronize();
            } else {
                FileReader fileReader = new FileReader(optionsFile);
                instance = gson.fromJson(fileReader, LauncherOptions.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("LauncherOptions init finished");
    }

    public static LauncherOptions getInstance() {
        System.out.println("LauncherOptions is "+instance);
        return instance;
    }

    public static File getDirectory() {
        return directory;
    }

    public void synchronize() {
        File optionsFile = new File(settings,"launcher_options.json");
        try (FileWriter fileWriter = new FileWriter(optionsFile)) {
            gson.toJson(this,fileWriter);
        } catch (JsonIOException | IOException e) {
            e.printStackTrace();
        }
    }

    private String playerName;
    private String versionID;

    public String getVersionID() {
        return versionID;
    }

    public void setVersionID(String versionID) {
        this.versionID = versionID;
        synchronize();
    }

    public void launch() {
        System.out.println("Launching Minecraft version "+versionID);
        Version version = VersionList.getInstance().versions.stream().filter(v -> v.id.equals(versionID)).findAny().orElseThrow();
        VersionSpecification versionSpecification = VersionSpecification.get(version.url);
        // TODO Temporarily disabled
        //PlayerProfile playerProfile = PlayerProfile.profiles.stream().filter(p -> p.name.equals(playerName)).findAny().orElseThrow();
        PlayerProfile playerProfile = new PlayerProfile("ItsNatalka2012");
        File versionFolder = new File(versions,version.id);
        versionFolder.mkdir();
        File versionFile = new File(versionFolder,"minecraft.jar");
        if (!versionFile.exists()) {
            System.out.println("File does not exist, downloading...");
            try {
                versionSpecification.downloads.client.download(versionFile.toPath());
            } catch (Exception e) {
                System.out.println("Launch failed, aborting");
                e.printStackTrace();
                return;
            }
            System.out.println("File downloaded successfully.");
        }
        StringBuilder classpathBuilder = new StringBuilder(versionFile.getAbsolutePath());
        for (Library l : versionSpecification.libraries) {
            classpathBuilder.append(l.install());
        }

        List<String> command = new ArrayList<>();
        command.add("java");
        if (versionSpecification.arguments != null) {
            for (Object o : versionSpecification.arguments.jvm) {
                if (o instanceof String) {
                    command.add((String) o);
                }
            }
        } else {
            command.add("-Djava.library.path=${natives_directory}");
            command.add("-Djna.tmpdir=${natives_directory}");
            command.add("-Dorg.lwjgl.system.SharedLibraryExtractPath=${natives_directory}");
            command.add("-Dio.netty.native.workdir=${natives_directory}");
            command.add("-cp");
            command.add("${classpath}");
        }
        command = command.stream().map(s -> s
                                   .replace("${natives_directory}",natives.getAbsolutePath())
                                   .replace("${launcher_name}","salwyrr;1.18.1;68ECBBCBBA1246A2878B7E47F7D9F09E")
                                   .replace("${launcher_version}", "1.0")
                                   .replace("${classpath}",classpathBuilder.toString())
        ).collect(Collectors.toList());
        command.add(versionSpecification.mainClass);
        if (versionSpecification.arguments != null) {
            for (Object o : versionSpecification.arguments.game) {
                if (o instanceof String) {
                    command.add((String) o);
                }
            }
        } else if (versionSpecification.minecraftArguments != null) {
            for (String arg : versionSpecification.minecraftArguments.split(" ")) {
                command.add(arg);
            }
        }
        
        command = command.stream().map(s -> s
                                   .replace("${auth_player_name}",playerProfile.name)
                                   .replace("${auth_uuid}",playerProfile.uuid)
                                   .replace("${auth_access_token}", playerProfile.accessToken)
                                   .replace("${assets_root}", assets.getAbsolutePath())
                                   .replace("${version_name}", version.id)
                                   .replace("${game_directory}", directory.getAbsolutePath())
                                   .replace("${assets_index_name}", version.id)
                                   .replace("${version_type}", version.type.toString())
                                   .replace("${clientid}", "salwyrr;1.18.1;68ECBBCBBA1246A2878B7E47F7D9F09E")
                                   .replace("${user_type}", "full")
                                   .replace("${auth_xuid}", "-")
                                   .replace("${user_properties}","{}")
        ).collect(Collectors.toList());
        System.out.println(String.join(" ",command));
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        try {
            processBuilder.redirectOutput(new File(logs,"output.txt"));
            processBuilder.redirectError(new File(logs,"error.txt"));
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}