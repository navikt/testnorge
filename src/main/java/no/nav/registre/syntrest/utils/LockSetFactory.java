package no.nav.registre.syntrest.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class LockSetFactory {
    public static Map<String, LockSet> getLockSet(String[] keyes) {
        Map<String, LockSet> lockset = new HashMap<>();
        for (String key : keyes) {
            lockset.put(key, new LockSet(new ReentrantLock(), new ReentrantLock()));
        }

        return lockset;
    }
}