package no.nav.testnav.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.levendearbeidsforholdansettelse.domain.dto.KanAnsettesDTO;
import no.nav.testnav.levendearbeidsforholdansettelse.entity.AnsettelseLogg;
import no.nav.testnav.levendearbeidsforholdansettelse.repository.AnsettelseLoggRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static no.nav.testnav.levendearbeidsforholdansettelse.entity.JobbParameterNavn.ARBEIDSFORHOLD_TYPE;
import static no.nav.testnav.levendearbeidsforholdansettelse.entity.JobbParameterNavn.STILLINGSPROSENT;

/**
 * Klasse for å lagre ansedttelsene som har blir gjort i ansettelse_ligg db.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnsettelseLoggService {

    private final AnsettelseLoggRepository ansettelseLoggRepository;

    /**
     * Funksjonen som lagrer ansettelsen i db
     *
     * @param kanAnsette inneholder ident og orgnummer
     * @param parametere Stillingsprosent og arbeidsavtalarbeidsforholdType til personen.
     */
    public Mono<AnsettelseLogg> lagreAnsettelse(KanAnsettesDTO kanAnsette, Map<String, String> parametere) {

        return ansettelseLoggRepository.save(AnsettelseLogg.builder()
                .isNew(true)
                .folkeregisterident(kanAnsette.getIdent())
                .organisasjonsnummer(kanAnsette.getOrgnummer())
                .timestamp(LocalDateTime.now())
                .arbeidsforholdType(parametere.get(ARBEIDSFORHOLD_TYPE.value))
                .stillingsprosent(Integer.parseInt(parametere.get(STILLINGSPROSENT.value)))
                .ansattfra(LocalDate.now())
                .build());
    }
}
