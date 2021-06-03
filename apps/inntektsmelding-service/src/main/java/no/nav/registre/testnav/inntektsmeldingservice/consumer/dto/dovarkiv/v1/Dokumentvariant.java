package no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.dovarkiv.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class Dokumentvariant {
    @JsonProperty
    private String filtype;
    @JsonProperty
    private String variantformat;
    @JsonProperty
    private byte[] fysiskDokument;
}
