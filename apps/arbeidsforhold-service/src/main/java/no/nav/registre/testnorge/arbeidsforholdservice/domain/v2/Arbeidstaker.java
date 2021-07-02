package no.nav.registre.testnorge.arbeidsforholdservice.domain.v2;

import lombok.AllArgsConstructor;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto.ArbeidstakerDTO;

@AllArgsConstructor
public class Arbeidstaker {

    private final ArbeidstakerDTO dto;

    public no.nav.registre.testnorge.arbeidsforholdservice.provider.v2.dto.ArbeidstakerDTO toDTO() {

        return no.nav.registre.testnorge.arbeidsforholdservice.provider.v2.dto.ArbeidstakerDTO.builder()
                .type(dto.getType().equals("Person") ? Aktoer.Person : Aktoer.Organisasjon)
                .offentligIdent(dto.getOffentligIdent())
                .build();
    }

}
