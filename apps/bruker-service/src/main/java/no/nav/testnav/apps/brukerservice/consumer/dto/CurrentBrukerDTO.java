package no.nav.testnav.apps.brukerservice.consumer.dto;

public record CurrentBrukerDTO(RepresentererTeam representererTeam) {

    public record RepresentererTeam(String brukerId) {
    }
}
