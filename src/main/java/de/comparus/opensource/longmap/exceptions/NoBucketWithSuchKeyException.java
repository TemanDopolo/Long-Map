package de.comparus.opensource.longmap.exceptions;

public class NoBucketWithSuchKeyException extends Exception {
    public NoBucketWithSuchKeyException(String message) {
        super(message);
    }
}
