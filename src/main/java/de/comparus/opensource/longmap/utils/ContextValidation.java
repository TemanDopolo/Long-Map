package de.comparus.opensource.longmap.utils;

public class ContextValidation {

    MapValidationStrategy strategy;

    public ContextValidation(MapValidationStrategy strategy) {
        this.strategy = strategy;
    }

    public void validateKey(long key) {
        strategy.validateKey(key);
    }

}
