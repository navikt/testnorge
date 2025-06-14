package no.nav.testnav.identpool.domain;

public enum BatchStatus {
    MINING_STARTED,
    @Deprecated
    MINING_COMPLETE, // Retained for backward compatibility
    MINING_COMPLETED,
    MINING_FAILED,
    CLEAN_STARTED,
    CLEAN_COMPLETED,
    CLEAN_FAILED
}