package no.nav.dolly.bestilling.henvendelse.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HenvendelseSamtalereferatRequest {

    protected String aktorId;
    protected String temagruppe;
    protected String tema;
    protected String enhet;
    protected String fritekst;

    @JsonIgnore
    protected String kjedeId;

    @JsonIgnore
    protected String type;
}
