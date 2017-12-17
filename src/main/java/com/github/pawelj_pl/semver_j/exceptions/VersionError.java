package com.github.pawelj_pl.semver_j.exceptions;

public class VersionError extends RuntimeException {
    
    public VersionError() {
    }
    
    public VersionError(String message) {
        super(message);
    }
    
    public VersionError(String message, Throwable cause) {
        super(message, cause);
    }
    
    public VersionError(Throwable cause) {
        super(cause);
    }
    
    public VersionError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
