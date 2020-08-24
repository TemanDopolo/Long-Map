package de.comparus.opensource.longmap.utils;

public class MapValidationStrategyImpl implements MapValidationStrategy {

    @Override
    public void validateKey(long key) {
        if (key < 0) {
            throw new IllegalArgumentException("Key cannot be negative!");
        }
    }
}
