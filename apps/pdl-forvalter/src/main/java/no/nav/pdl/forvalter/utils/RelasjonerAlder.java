package no.nav.pdl.forvalter.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BestillingRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@UtilityClass
public class RelasjonerAlder {

    public BestillingRequestDTO fixRelasjonerAlder(BestillingRequestDTO request) {

        request.getPerson().getForelderBarnRelasjon().stream()
                .filter(ForelderBarnRelasjonDTO::isForeldre)
                .map(relasjon -> {
                    if (relasjon.isEksisterendePerson()) {
                        return getAlder(DatoFraIdentUtility.getDato(relasjon.getRelatertPerson()));
                    } else if (nonNull(relasjon.getRelatertPersonUtenFolkeregisteridentifikator())) {
                        return getAlder(relasjon.getRelatertPersonUtenFolkeregisteridentifikator().getFoedselsdato());
                    } else if (nonNull(relasjon.getNyRelatertPerson())) {
                        return getAlderNyPerson(relasjon.getNyRelatertPerson(), 18);
                    } else {
                        return 18;
                    }
                })
                .max(Comparator.comparing(integer -> integer))
                .ifPresent(eldsteBarn -> {
                    if (isNull(request.getAlder()) && isNull(request.getFoedtEtter())) {
                        request.setFoedtFoer(LocalDateTime.now().minusYears(18 + eldsteBarn));
                    }
                    request.getPerson().getSivilstand().stream()
                            .filter(SivilstandDTO::isGiftOrSamboer)
                            .forEach(partner -> {
                                if (nonNull(partner.getNyRelatertPerson()) &&
                                        isNull(partner.getNyRelatertPerson().getAlder()) &&
                                        isNull(partner.getNyRelatertPerson().getFoedtEtter())) {
                                    partner.getNyRelatertPerson().setFoedtFoer(LocalDateTime.now().minusYears(
                                            getAlderNyPerson(partner.getNyRelatertPerson(), 18 + eldsteBarn)));
                                }
                            });
                });

        return request;
    }

    private static int getAlderNyPerson(PersonRequestDTO relasjon, int nyAlder) {

        if (nonNull(relasjon.getAlder())) {
            return relasjon.getAlder();

        } else if (isNull(relasjon.getFoedtEtter()) && isNull(relasjon.getFoedtFoer())) {
            return nyAlder;

        } else if (nonNull(relasjon.getFoedtEtter())) {
            return getAlder(relasjon.getFoedtEtter());

        } else {
            return Math.max(getAlder(relasjon.getFoedtFoer()), nyAlder);
        }
    }

    private int getAlder(LocalDate start) {

        return (int) ChronoUnit.YEARS.between(start, LocalDateTime.now());
    }

    private int getAlder(LocalDateTime start) {

        return getAlder(start.toLocalDate());
    }
}
