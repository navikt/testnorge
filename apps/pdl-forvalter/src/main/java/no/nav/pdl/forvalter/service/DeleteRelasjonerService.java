package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.RelasjonRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.AVDOEDD_FOR_KONTAKT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.EKTEFELLE_PARTNER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FALSK_IDENTITET;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FAMILIERELASJON_BARN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FAMILIERELASJON_FORELDER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FORELDREANSVAR_BARN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FORELDREANSVAR_FORELDER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FULLMAKTSGIVER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FULLMEKTIG;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.GAMMEL_IDENTITET;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.KONTAKT_FOR_DOEDSBO;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.NY_IDENTITET;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.RIKTIG_IDENTITET;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.VERGE;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.VERGE_MOTTAKER;

@Service
@RequiredArgsConstructor
public class DeleteRelasjonerService {

    private final RelasjonRepository relasjonRepository;

    public Mono<Void> deleteRelasjoner(DbPerson person, DbPerson relatertPerson, RelasjonType type) {

        return relasjonRepository.deleteByPersonIdAndRelatertPersonIdAndRelasjonTypeIn(
                        person.getId(),
                        relatertPerson.getId(),
                        getRelasjonTyper(type))
                .then(relasjonRepository.deleteByPersonIdAndRelatertPersonIdAndRelasjonTypeIn(
                        relatertPerson.getId(),
                        person.getId(),
                        getRelasjonTyper(type)));
    }

    private static List<RelasjonType> getRelasjonTyper(RelasjonType relasjonType) {

        return switch (relasjonType) {

            case FULLMEKTIG, FULLMAKTSGIVER -> List.of(FULLMEKTIG, FULLMAKTSGIVER);
            case VERGE, VERGE_MOTTAKER -> List.of(VERGE, VERGE_MOTTAKER);
            case RIKTIG_IDENTITET, FALSK_IDENTITET -> List.of(RIKTIG_IDENTITET, FALSK_IDENTITET);
            case KONTAKT_FOR_DOEDSBO, AVDOEDD_FOR_KONTAKT ->
                    List.of(KONTAKT_FOR_DOEDSBO, AVDOEDD_FOR_KONTAKT);
            case NY_IDENTITET, GAMMEL_IDENTITET -> List.of(NY_IDENTITET, GAMMEL_IDENTITET);
            case EKTEFELLE_PARTNER -> List.of(EKTEFELLE_PARTNER);
            case FAMILIERELASJON_BARN, FAMILIERELASJON_FORELDER ->
                    List.of(FAMILIERELASJON_BARN, FAMILIERELASJON_FORELDER);
            case FORELDREANSVAR_BARN, FORELDREANSVAR_FORELDER ->
                    List.of(FORELDREANSVAR_BARN, FORELDREANSVAR_FORELDER);
        };
    }
}