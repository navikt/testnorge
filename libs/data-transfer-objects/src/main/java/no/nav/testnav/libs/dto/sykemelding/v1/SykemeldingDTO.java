package no.nav.testnav.libs.dto.sykemelding.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@AllArgsConstructor
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
    private List<PeriodeDTO> perioder;
    @JsonProperty
    private OrganisasjonDTO sender;
    @JsonProperty
    private OrganisasjonDTO mottaker;
    @JsonProperty
    private DiagnoseDTO hovedDiagnose;
    private List<DiagnoseDTO> biDiagnoser;
    @JsonProperty
    private DetaljerDTO detaljer;
    @JsonProperty
    private Boolean umiddelbarBistand;
    private List<UtdypendeOpplysningerDTO> utdypendeOpplysninger;

    @JsonIgnore
    public List<DiagnoseDTO> getBiDiagnoser() {
        if (biDiagnoser == null) {
            return Collections.emptyList();
        }
        return biDiagnoser;
    }

    public List<PeriodeDTO> getPerioder() {

        if (isNull(perioder)) {
            perioder = new ArrayList<>();
        }
        return perioder;
    }


    public List<UtdypendeOpplysningerDTO> getUtdypendeOpplysninger() {

        if (isNull(utdypendeOpplysninger)) {
            utdypendeOpplysninger = new ArrayList<>();
        }
        return utdypendeOpplysninger;
    }


    @JsonIgnore
    public Boolean getManglendeTilretteleggingPaaArbeidsplassen() {
        return manglendeTilretteleggingPaaArbeidsplassen != null && manglendeTilretteleggingPaaArbeidsplassen;
    }
}
