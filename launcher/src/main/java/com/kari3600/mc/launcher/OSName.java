package com.kari3600.mc.launcher;

public enum OSName {
    windows,
    osx,
    linux;
    public boolean isCurrentSystem() {
        return System.getProperty("os.name").toLowerCase().contains(this.toString());
    }
    public static OSName getCurrentSystem() {
        for (OSName osName : OSName.values()) {
            if (System.getProperty("os.name").toLowerCase().contains(osName.toString())) {
                return osName;
            }
        }
        return null;
    }
    public static String getCurrentArchitecture() {
        String arch = System.getProperty("os.arch");
        return arch.substring(arch.length()-2, arch.length());
    }
}
