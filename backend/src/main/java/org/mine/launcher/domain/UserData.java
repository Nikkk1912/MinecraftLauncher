package org.mine.launcher.domain;

public class UserData {
    private final String playerName;
    private final String uuid;
    private final String accessToken;
    private final String gameDirectory;
    private final String assetsDirectory;
    private final String assetsIndex;
    private final String versionName;
    private final String launcherName;
    private final String launcherVersion;

    public UserData(String playerName, String uuid, String accessToken, String gameDirectory, String assetsDirectory, String assetsIndex, String versionName, String launcherName, String launcherVersion) {
        this.playerName = playerName;
        this.uuid = uuid;
        this.accessToken = accessToken;
        this.gameDirectory = gameDirectory;
        this.assetsDirectory = assetsDirectory;
        this.assetsIndex = assetsIndex;
        this.versionName = versionName;
        this.launcherName = launcherName;
        this.launcherVersion = launcherVersion;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getUuid() {
        return uuid;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getGameDirectory() {
        return gameDirectory;
    }

    public String getAssetsDirectory() {
        return assetsDirectory;
    }

    public String getAssetsIndex() {
        return assetsIndex;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getLauncherName() {
        return launcherName;
    }

    public String getLauncherVersion() {
        return launcherVersion;
    }
}
