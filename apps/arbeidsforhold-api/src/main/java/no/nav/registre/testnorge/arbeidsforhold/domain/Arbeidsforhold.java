package no.nav.registre.testnorge.arbeidsforhold.domain;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.testnorge.arbeidsforhold.consumer.dto.ArbeidsforholdDTO;
import no.nav.registre.testnorge.arbeidsforhold.exception.ArbeidsforholdNotFoundException;

@Slf4j
@Getter
public class Arbeidsforhold {
    private final String arbeidsforholdId;
    private final String orgnummer;
    private final Double stillingsprosent;
    private final String yrke;

    public Arbeidsforhold(ArbeidsforholdDTO dto) {
        arbeidsforholdId = dto.getArbeidsforholdId();
        orgnummer = dto.getArbeidsgiver().getOrganisasjonsnummer();


        if(dto.getArbeidsavtaler().isEmpty()){
            throw new ArbeidsforholdNotFoundException("Finner ikke arbeidsforhold");
        }

        if (dto.getArbeidsavtaler().size() > 1) {
            log.warn("Fant flere arbeidsavtaler. Velger den første i listen");
        }


        var arbeidsavtale = dto.getArbeidsavtaler().get(0);
        stillingsprosent = arbeidsavtale.getStillingsprosent();
        yrke = arbeidsavtale.getYrke();
    }

    public no.nav.registre.testnorge.dto.arbeidsforhold.v1.ArbeidsforholdDTO toDTO() {
        return no.nav.registre.testnorge.dto.arbeidsforhold.v1.ArbeidsforholdDTO
                .builder()
                .arbeidsforholdId(arbeidsforholdId)
                .orgnummer(orgnummer)
                .stillingsprosent(stillingsprosent)
                .yrke(yrke)
                .build();
    }
}
