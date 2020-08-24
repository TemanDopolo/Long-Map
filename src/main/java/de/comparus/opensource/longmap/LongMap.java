package de.comparus.opensource.longmap;

import de.comparus.opensource.longmap.exceptions.NoBucketWithSuchKeyException;

public interface LongMap<V> {
    V put(long key, V value);
    V get(long key);
    V remove(long key) throws NoBucketWithSuchKeyException;

    boolean isEmpty();
    boolean containsKey(long key);
    boolean containsValue(V value);

    long[] keys();
    V[] values();

    long size();
    void clear();
}
