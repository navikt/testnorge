package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.OrganisasjonDTO;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.pdl.Ident;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.AnsettelseLogg;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.repository.AnsettelseLoggRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class AnsettelseLoggService {
    //private final double stillingsprosent = 100.0;
    private final String arbeidsforholdType = "ordineartArbeidsfohold";
    private final AnsettelseLoggRepository ansettelseLoggRepository;
    public void lagreAnsettelse(Ident person, OrganisasjonDTO org, Double stillingsprosent){
        AnsettelseLogg ansettelseLogg = AnsettelseLogg.builder()
                .folkeregisterident(person.getIdent())
                .organisasjonsnummer(org.getOrgnummer())
                .timestamp(OffsetDateTime.now())
                .arbeidsforholdType(arbeidsforholdType)
                .stillingsprosent(BigDecimal.valueOf(stillingsprosent))
                .ansattfra(LocalDate.now())
                .build();
        ansettelseLoggRepository.save(ansettelseLogg);
    }
}
