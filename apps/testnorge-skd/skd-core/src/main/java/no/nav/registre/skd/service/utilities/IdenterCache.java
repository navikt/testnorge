package no.nav.registre.skd.service.utilities;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

@Service
public class IdenterCache {

    private static final int UPDATE_INTERVAL_IN_HOURS = 12;

    private Set<String> identerCache;
    private LocalDateTime lastUpdated;

    public IdenterCache() {
        this.identerCache = new HashSet<>();
        this.lastUpdated = LocalDateTime.now();
    }

    public Set<String> getIdenterCache() {
        if (ChronoUnit.HOURS.between(lastUpdated, LocalDateTime.now()) > UPDATE_INTERVAL_IN_HOURS) {
            this.identerCache = new HashSet<>();
            this.lastUpdated = LocalDateTime.now();
        }
        return identerCache;
    }

    public void addToCache(Set<String> identer) {
        if (identerCache == null) {
            identerCache = new HashSet<>();
        }
        identerCache.addAll(identer);
        lastUpdated = LocalDateTime.now();
    }
}
