package no.nav.pdl.forvalter.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BestillingRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Objects;
import java.util.Random;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@UtilityClass
public class RelasjonerAlder {

    private static final Random RANDOM = new SecureRandom();

    public BestillingRequestDTO fixRelasjonerAlder(BestillingRequestDTO request) {

        request.getPerson().getForelderBarnRelasjon().stream()
                .filter(ForelderBarnRelasjonDTO::isForeldre)
                .map(relasjon -> {
                    if (isNotBlank(relasjon.getRelatertPerson())) {
                        return getAlder(DatoFraIdentUtility.getDato(relasjon.getRelatertPerson()));
                    } else if (nonNull(relasjon.getNyRelatertPerson())) {
                        return getAlderNyPerson(relasjon.getNyRelatertPerson());
                    } else if (nonNull(relasjon.getRelatertPersonUtenFolkeregisteridentifikator())) {
                        return getAlder(relasjon.getRelatertPersonUtenFolkeregisteridentifikator().getFoedselsdato());
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .max(Comparator.comparing(integer -> integer))
                .map(Integer::longValue)
                .ifPresent(eldsteBarn -> {
                    if (isNull(request.getAlder()) && isNull(request.getFoedtFoer())) {
                        request.setFoedtFoer(adjustDate(LocalDateTime.now(), 18 + eldsteBarn, request.getFoedtEtter()));
                    }
                    request.getPerson().getSivilstand().stream()
                            .filter(SivilstandDTO::isGiftOrSamboer)
                            .forEach(partner -> {
                                if (isBlank(partner.getRelatertVedSivilstand())) {
                                    if (isNull(partner.getNyRelatertPerson())) {
                                        partner.setNyRelatertPerson(new PersonRequestDTO());
                                    }
                                    if (isNull(partner.getNyRelatertPerson().getAlder()) &&
                                            isNull(partner.getNyRelatertPerson().getFoedtFoer())) {
                                        partner.getNyRelatertPerson()
                                                .setFoedtFoer(adjustDate(LocalDateTime.now(), 18 + eldsteBarn,
                                                        partner.getNyRelatertPerson().getFoedtEtter()));
                                    }
                                }
                            });
                });

        request.getPerson()
                .getForelderBarnRelasjon().stream()
                .filter(ForelderBarnRelasjonDTO::isBarn)
                .filter(forelder -> request.hasAlder())
                .forEach(forelder -> {
                    if (isBlank(forelder.getRelatertPerson())) {
                        if (isNull(forelder.getNyRelatertPerson())) {
                            forelder.setNyRelatertPerson(new PersonRequestDTO());
                        }
                        if (isNull(forelder.getNyRelatertPerson().getAlder()) &&
                                isNull(forelder.getNyRelatertPerson().getFoedtFoer())) {
                            forelder.getNyRelatertPerson()
                                    .setFoedtFoer(adjustDate(LocalDateTime.now(), 18L + getAlder(request),
                                            forelder.getNyRelatertPerson().getFoedtEtter()));
                        }
                    }
                });

        return request;
    }

    private static int getAlder(BestillingRequestDTO request) {

        if (nonNull(request.getAlder())) {

            return request.getAlder();

        } else if (nonNull(request.getFoedtFoer())) {

            return getAlder(request.getFoedtFoer());

        } else {

            return getAlder(request.getFoedtEtter());
        }
    }

    private static LocalDateTime adjustDate(LocalDateTime foedtFoer, long minusYears, LocalDateTime foedtEtter) {

        if (isNull(foedtEtter)) {

            return foedtFoer.minusYears(minusYears);

        } else {

            return foedtFoer.minusYears(minusYears).isBefore(foedtEtter) ? foedtEtter : foedtFoer.minusYears(minusYears);
        }
    }

    private static Integer getAlderNyPerson(PersonRequestDTO relasjon) {

        if (nonNull(relasjon.getAlder())) {
            return relasjon.getAlder();

        } else if (isNull(relasjon.getFoedtEtter()) && isNull(relasjon.getFoedtFoer())) {
            return null;

        } else if (nonNull(relasjon.getFoedtEtter()) && nonNull(relasjon.getFoedtFoer())) {

            return Math.max(RANDOM.nextInt(getAlder(relasjon.getFoedtEtter())), getAlder(relasjon.getFoedtFoer()));

        } else if (nonNull(relasjon.getFoedtEtter())) {

            return RANDOM.nextInt(getAlder(relasjon.getFoedtEtter()));

        } else {

            return getAlder(relasjon.getFoedtFoer());
        }
    }

    private int getAlder(LocalDate start) {

        return (int) ChronoUnit.YEARS.between(start, LocalDateTime.now());
    }

    private int getAlder(LocalDateTime start) {

        return getAlder(start.toLocalDate());
    }
}
