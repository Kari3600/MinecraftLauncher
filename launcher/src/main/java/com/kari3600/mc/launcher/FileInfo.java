package com.kari3600.mc.launcher;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileInfo {
    String path;
    String sha1;
    long size;
    String url;
    public void download(File file) throws Exception {
        // Create a URL object from the file URL
        URL fileurl = new URI(url).toURL();

        HttpURLConnection connection = (HttpURLConnection) fileurl.openConnection();
        connection.setRequestMethod("GET");
            
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        InputStream inputStream = connection.getInputStream();
        file.getParentFile().mkdirs();
        file.createNewFile();
        Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        connection.disconnect();
    }
}
