package no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.dovarkiv.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DokumentInfo {
    @JsonProperty
    private String brevkode;
    @JsonProperty
    private String dokumentInfoId;
    @JsonProperty
    private String tittel;
}
