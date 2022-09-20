package no.nav.registre.testnorge.helsepersonellservice.domain;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class Persondata {
    private final PdlPersonBolk.PersonBolk pdlPerson;

    public String getIdent() {
        return pdlPerson.getIdent();
    }

    private Optional<PdlPersonBolk.Navn> getNavn() {
        return pdlPerson.getPerson().getNavn()
                .stream()
                .filter(value -> !value.getMetadata().getHistorisk())
                .findFirst();
    }

    public String getFornavn() {
        return getNavn().map(PdlPersonBolk.Navn::getFornavn).orElse(null);
    }

    public String getMellomnavn() {
        return getNavn().map(PdlPersonBolk.Navn::getMellomnavn).orElse(null);
    }

    public String getEtternavn() {
        return getNavn().map(PdlPersonBolk.Navn::getEtternavn).orElse(null);
    }

}
