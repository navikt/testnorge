package no.nav.registre.populasjoner.kafka.person;

/**
 * Marker interface
 */
public interface Dto {

    default MetadataDto getMetadata() {
        return null;
    }

    default FolkeregistermetadataDto getFolkeregistermetadata() {
        return null;
    }
}