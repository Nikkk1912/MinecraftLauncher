package org.mine.launcher.util;

public class OsDetector {
    public static String detectPlatform() {
        String osName = System.getProperty("os.name").toLowerCase();
        String osArch = System.getProperty("os.arch").toLowerCase();

        if (osName.contains("windows")) {
            if (osArch.contains("arm")) {
                return "windows-arm64";
            } else {
                String bits = System.getProperty("sun.arch.data.model");
                if (bits.contains("64")) {
                    return "windows-x64";
                } else {
                    return "windows-86";
                }
            }
        } else if (osName.contains("mac")) {
            if (osArch.contains("arm")) {
                return "mac-os-arm64";
            } else {
                return "mac-os";
            }
        } else if (osName.contains("linux-i386")) {
            return "linux-i386";
        } else if (osName.contains("linux")) {
            return "linux";
        }
        return "linux";
    }
}
