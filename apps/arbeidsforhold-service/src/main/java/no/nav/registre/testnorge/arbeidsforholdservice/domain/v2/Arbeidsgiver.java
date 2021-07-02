package no.nav.registre.testnorge.arbeidsforholdservice.domain.v2;

import lombok.AllArgsConstructor;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto.ArbeidsgiverDTO;

@AllArgsConstructor
public class Arbeidsgiver {

    private final ArbeidsgiverDTO dto;

    public no.nav.registre.testnorge.arbeidsforholdservice.provider.v2.dto.ArbeidsgiverDTO toDTO() {
        var builder = no.nav.registre.testnorge.arbeidsforholdservice.provider.v2.dto.ArbeidsgiverDTO.builder();

        if (dto.getType().equals("Organisasjon")) {
            builder.type(Aktoer.Organisasjon)
                    .organisasjonsnummer(dto.getOrganisasjonsnummer());
        } else {
            builder.type(Aktoer.Person)
                    .offentligIdent(dto.getIdent());
        }

        return builder.build();
    }

}
