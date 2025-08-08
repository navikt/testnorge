package no.nav.testnav.apps.brukerservice.domain;

import no.nav.testnav.apps.brukerservice.dto.BrukerDTO;
import no.nav.testnav.apps.brukerservice.repository.UserEntity;

import java.time.LocalDateTime;

public class User {
    private final String id;
    private final String brukernavn;
    private final String epost;
    private final String organisasjonsnummer;
    private final LocalDateTime opprett;
    private final LocalDateTime sisteLoggetinn;

    public User(UserEntity entity) {
        this.organisasjonsnummer = entity.getOrganisasjonsnummer();
        this.id = entity.getId();
        this.brukernavn = entity.getBrukernavn();
        this.epost = entity.getEpost();
        this.opprett = entity.getOpprettet();
        this.sisteLoggetinn = entity.getSistInnlogget();
    }

    public String getId() {
        return id;
    }

    public String getBrukernavn() {
        return brukernavn;
    }

    public String getOrganisasjonsnummer() {
        return organisasjonsnummer;
    }

    public UserEntity create() {
        var entity = new UserEntity();
        entity.setId(id);
        entity.setBrukernavn(brukernavn);
        entity.setNew(true);
        return entity;
    }

    public BrukerDTO toDTO() {
        return new BrukerDTO(id, brukernavn, organisasjonsnummer, epost, opprett, sisteLoggetinn);
    }
}
