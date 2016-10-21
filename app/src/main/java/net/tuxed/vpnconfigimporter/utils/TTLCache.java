package net.tuxed.vpnconfigimporter.utils;

import android.util.Pair;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A cache which can purge its contents after a while.
 * Accessing a value in the list will NOT reset its TTL time.
 * Created by Daniel Zolnai on 2016-10-20.
 */
public class TTLCache<T> {

    private Map<String, Pair<Date, T>> _entries = new HashMap<>();

    private final long _purgeAfterSeconds;

    private final Object _lock = new Object();

    /**
     * Constructor for a new cache.
     *
     * @param purgeAfterSeconds The amount of seconds required to elapse until an entry can be purged.
     */
    public TTLCache(long purgeAfterSeconds) {
        _purgeAfterSeconds = purgeAfterSeconds;
    }

    /**
     * Constructor for restoring the data of a previous cache.
     *
     * @param originalData      The original data.
     * @param purgeAfterSeconds The amount of seconds required to elapse until an entry can be purged.
     */
    public TTLCache(Map<String, Pair<Date, T>> originalData, long purgeAfterSeconds) {
        _purgeAfterSeconds = purgeAfterSeconds;
        _entries = new HashMap<>();
        _entries.putAll(originalData);
    }

    /**
     * Puts an object in the cache.
     *
     * @param key   The key to register the object by.
     * @param entry The value to store.
     */
    public void put(String key, T entry) {
        synchronized (_lock) {
            _entries.put(key, new Pair<>(new Date(), entry));
        }
    }

    /**
     * Returns the unmodifiable map of entries inside the cache.
     *
     * @return The entries stored in the cache. Only reading these values is allowed.
     */
    public Map<String, Pair<Date, T>> getEntries() {
        synchronized (_lock) {
            return Collections.unmodifiableMap(_entries);
        }
    }

    /**
     * Returns a value from the cache.
     *
     * @param key The key the value is inserted by.
     * @return The value if found, otherwise null.
     */
    public T get(String key) {
        Pair<Date, T> result;
        synchronized (_lock) {
            result = _entries.get(key);
        }
        if (result != null) {
            return result.second;
        } else {
            return null;
        }
    }

    /**
     * Purges the entries from the list, where the TTL time has exceeded the preset limit.
     */
    public void purge() {
        synchronized (_lock) {
            Iterator<Map.Entry<String, Pair<Date, T>>> entryIterator = _entries.entrySet().iterator();
            final Date now = new Date();
            while (entryIterator.hasNext()) {
                Date entryInserted = entryIterator.next().getValue().first;
                long secondsDiff = (now.getTime() - entryInserted.getTime()) / 1000;
                if (secondsDiff >= _purgeAfterSeconds) {
                    entryIterator.remove();
                }
            }
        }
    }

    /**
     * Returns the amount of seconds an entry is allowed in the cache.
     *
     * @return The minimum amount of seconds an entry is guaranteed to reside inside the cache.
     */
    public long getPurgeAfterSeconds() {
        return _purgeAfterSeconds;
    }
}