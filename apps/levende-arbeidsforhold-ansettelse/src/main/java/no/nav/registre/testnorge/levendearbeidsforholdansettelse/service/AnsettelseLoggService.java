package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.dto.OrganisasjonDTO;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.pdl.Ident;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.AnsettelseLogg;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.repository.AnsettelseLoggRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Klasse for å lagre ansedttelsene som har blir gjort i ansettelse_ligg db.
 */
@Service
@RequiredArgsConstructor
public class AnsettelseLoggService {

    private final AnsettelseLoggRepository ansettelseLoggRepository;

    /**
     * Funksjonen som lagrer ansettelsen i db
     * @param person Ident objektet til personen som har blir ansatt.
     * @param org OrgaisasjonDTO objektet til organisasjonen personen har
     *            blitt ansatt i.
     * @param stillingsprosent Stillingsprosenten i arbeidsavtalen til personen.
     * @param arbeidsforholdType Arbeidsforholdtypen i arbeidsatalen til personen.
     */
    public void lagreAnsettelse(Ident person, OrganisasjonDTO org, Double stillingsprosent, String arbeidsforholdType){
        AnsettelseLogg ansettelseLogg = AnsettelseLogg.builder()
                .folkeregisterident(person.getIdent())
                .organisasjonsnummer(org.getOrganisasjonsnummer())
                .timestamp(OffsetDateTime.now())
                .arbeidsforholdType(arbeidsforholdType)
                .stillingsprosent(BigDecimal.valueOf(stillingsprosent))
                .ansattfra(LocalDate.now())
                .build();
        ansettelseLoggRepository.save(ansettelseLogg);
    }
}
