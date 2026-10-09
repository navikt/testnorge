package no.nav.dolly.bestilling.henvendelse.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HenvendelseSamtalereferatRequest {

    private String aktorId;
    private String temagruppe;
    private String tema;
    private String enhet;
    private String fritekst;

    @JsonIgnore
    private String kjedeId;

    @JsonIgnore
    private String type;
}
