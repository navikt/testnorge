package no.nav.registre.inntekt.provider.rs.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.inntekt.domain.dokmot.ProsessertInntektDokument;

@Value
public class AltinnInntektResponse {

    @JsonProperty
    private final String fnr;
    @JsonProperty
    private final List<ProsessertInntektDokumentDTO> dokumenter;

    public AltinnInntektResponse(
            String fnr,
            List<ProsessertInntektDokument> dokumentList,
            boolean includeXml
    ) {
        this.fnr = fnr;
        this.dokumenter = dokumentList
                .stream()
                .map(dokument -> new ProsessertInntektDokumentDTO(dokument, includeXml))
                .collect(Collectors.toList());
    }
}
