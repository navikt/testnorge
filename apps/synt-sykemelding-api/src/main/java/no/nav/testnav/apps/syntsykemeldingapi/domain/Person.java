package no.nav.testnav.apps.syntsykemeldingapi.domain;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import no.nav.testnav.apps.syntsykemeldingapi.domain.pdl.PdlPerson;
import no.nav.testnav.apps.syntsykemeldingapi.domain.pdl.WithMetadata;

@Builder
@RequiredArgsConstructor
public class Person {
    private final PdlPerson pdlPerson;

    public String getIdent() {
        return pdlPerson.getData().getHentIdenter().getIdenter().stream()
                .filter(value -> value.getGruppe() == PdlPerson.Gruppe.FOLKEREGISTERIDENT && !value.isHistorisk())
                .findFirst()
                .map(PdlPerson.Identer::getIdent)
                .orElse(null);
    }

    private Optional<PdlPerson.Navn> getNavn() {
        return getCurrent(pdlPerson.getData().getHentPerson().getNavn());
    }

    public String getFornavn() {
        return getNavn().map(PdlPerson.Navn::getFornavn).orElse(null);
    }

    public String getMellomnavn() {
        return getNavn().map(PdlPerson.Navn::getMellomnavn).orElse(null);
    }

    public String getEtternavn() {
        return getNavn().map(PdlPerson.Navn::getEtternavn).orElse(null);
    }

    public LocalDate getFoedselsdato() {
        return getCurrent(pdlPerson.getData().getHentPerson().getFoedsel())
                .map(PdlPerson.Foedsel::getFoedselsdato).orElse(null);
    }

    private static <T extends WithMetadata> Optional<T> getCurrent(List<T> list) {
        if (list == null) {
            return Optional.empty();
        }
        return list.stream()
                .filter(value -> !value.getMetadata().getHistorisk())
                .findFirst();
    }
}
