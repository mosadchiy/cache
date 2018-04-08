package com.company;

/**
 * Implementation of {@link IEvictionDataFactory}
 * <p>
 * Factory of {@link EvictionData}
 */
public class EvictionDataFactory implements IEvictionDataFactory {
    @Override
    public IEvictionData createEvictionData() {
        return new EvictionData();
    }
}
