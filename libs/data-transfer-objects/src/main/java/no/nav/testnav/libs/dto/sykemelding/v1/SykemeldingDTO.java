package no.nav.testnav.libs.dto.sykemelding.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode
public class SykemeldingDTO {

    @JsonProperty
    private LocalDate startDato;
    @JsonProperty
    private PasientDTO pasient;
    @JsonProperty
    private HelsepersonellDTO helsepersonell;
    @JsonProperty
    private ArbeidsgiverDTO arbeidsgiver;
    @JsonProperty
    private Boolean manglendeTilretteleggingPaaArbeidsplassen;
    @JsonProperty
    private List<PeriodeDTO> perioder;
    @JsonProperty
    private OrganisasjonDTO sender;
    @JsonProperty
    private OrganisasjonDTO mottaker;
    @JsonProperty
    private DiagnoseDTO hovedDiagnose;
    @JsonProperty
    private List<DiagnoseDTO> biDiagnoser;
    @JsonProperty
    private DetaljerDTO detaljer;
    @JsonProperty
    private Boolean umiddelbarBistand;

    @JsonIgnore
    public List<DiagnoseDTO> getBiDiagnoser() {
        if (biDiagnoser == null) {
            return Collections.emptyList();
        }
        return biDiagnoser;
    }

    @JsonIgnore
    public Boolean getManglendeTilretteleggingPaaArbeidsplassen() {
        return manglendeTilretteleggingPaaArbeidsplassen != null && manglendeTilretteleggingPaaArbeidsplassen;
    }
}
