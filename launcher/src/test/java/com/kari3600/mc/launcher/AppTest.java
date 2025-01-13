package com.kari3600.mc.launcher;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.kari3600.mc.launcher.Version.VersionType;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

public class AppTest {
    static Stream<Version> provideVersions() {
        return VersionList.getInstance().versions.stream().filter(v -> v.type==VersionType.release);
   
    }
    @ParameterizedTest
    @MethodSource("provideVersions")
    public void testAllVersions(Version version) {
        assertNotNull(version.id, "Version ID should not be null");
        assertNotNull(version.url, "Version URL should not be null");
        LauncherOptions options = new LauncherOptions();
        options.setVersionID(version.id);
        options.launch();
    }
}
