package com.kari3600.mc.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Library {
    private static final File libraries = new File(LauncherOptions.getDirectory(),"libraries");
    private static final File nativesDir = new File(LauncherOptions.getDirectory(),"natives");

    class LibraryDownloads {
        FileInfo artifact;
        Map<String,FileInfo> classifiers;
    }
    LibraryDownloads downloads;
    String name;
    List<Rule> rules;
    EnumMap<OSName,String> natives;
    private String checkLib(FileInfo fileInfo) {
        boolean nativeLib = fileInfo.path.contains("natives");
        File libFile = (nativeLib ? new File(nativesDir,fileInfo.path) : new File(libraries,fileInfo.path));
        libFile.getParentFile().mkdirs();
        if (!libFile.exists()) {
            System.out.println("File does not exist, downloading...");
            try {
                fileInfo.download(libFile.toPath());
            } catch (Exception e) {
                System.out.println("Launch failed, aborting");
                e.printStackTrace();
                System.exit(1);
            }
            System.out.println("File downloaded successfully.");
        } else {
            System.out.println("Already exists.");
        }
        if (nativeLib) {
            System.out.println("Extracting native libraries");
            try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(libFile))) {
                ZipEntry entry;
                while ((entry = zipIn.getNextEntry()) != null) {
                    if (!entry.isDirectory() && entry.getName().endsWith(".dll")) {
                        String[] filePath = entry.getName().split("/");
                        System.out.println(entry.getName());
                        File outputFile = new File(nativesDir, filePath[filePath.length-1]);
                        System.out.println(outputFile.getAbsolutePath());
                        outputFile.createNewFile();
                        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = zipIn.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Launch failed, aborting");
                e.printStackTrace();
                System.exit(1);
            }
            System.out.println("Extracted successfully.");
        }
        return ";"+libFile.getAbsolutePath();
    }
    public String install() {
        String ret = "";
        System.out.println("Checking "+name);
        if (rules != null && !Rule.checkRules(rules)) {
            System.out.println("Ruled out");
            return "";
        }
        if (downloads.artifact != null) {
            ret = ret+checkLib(downloads.artifact);
        }
        if (natives != null) {
            if (!natives.containsKey(OSName.getCurrentSystem())) {
                System.out.println("Not native");
                return "";
            } else {
                String downloadName = natives.get(OSName.getCurrentSystem()).replace("${arch}",OSName.getCurrentArchitecture());
                System.out.println("Download named: "+downloadName);
                ret = ret+checkLib(downloads.classifiers.get(downloadName));
            }
        }
        return ret;
    }
}
