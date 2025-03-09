package org.mine.launcher.exceptions;

public class NoInstalledVersionException extends RuntimeException {
    public NoInstalledVersionException() {
        super("There are no installed versions in .minecraft folder");
    }
}
