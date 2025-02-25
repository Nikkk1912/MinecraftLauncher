package org.mine.launcher.exceptions;

public class VersionNotFoundInManifestException extends RuntimeException{
    public VersionNotFoundInManifestException(String version) {
        super("Could not find " + version + " in version manifest");
    }
}
