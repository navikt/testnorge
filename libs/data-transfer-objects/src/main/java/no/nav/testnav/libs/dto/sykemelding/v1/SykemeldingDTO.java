package no.nav.testnav.libs.dto.sykemelding.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SykemeldingDTO {

    private LocalDate startDato;
    private PasientDTO pasient;
    private HelsepersonellDTO helsepersonell;
    private ArbeidsgiverDTO arbeidsgiver;
    private Boolean manglendeTilretteleggingPaaArbeidsplassen;
    private List<PeriodeDTO> perioder;
    private OrganisasjonDTO sender;
    private OrganisasjonDTO mottaker;
    private DiagnoseDTO hovedDiagnose;
    private List<DiagnoseDTO> biDiagnoser;
    private DetaljerDTO detaljer;
    private List<UtdypendeOpplysningerDTO> utdypendeOpplysninger;
    private Boolean umiddelbarBistand;
    private KontaktMedPasientDTO kontaktMedPasient;

    public List<PeriodeDTO> getPerioder() {

        if (isNull(perioder)) {
            perioder = new ArrayList<>();
        }
        return perioder;
    }

    public List<DiagnoseDTO> getBiDiagnoser() {

        if (isNull(biDiagnoser)) {
            biDiagnoser = new ArrayList<>();
        }
        return biDiagnoser;
    }

    public List<UtdypendeOpplysningerDTO> getUtdypendeOpplysninger() {

        if (isNull(utdypendeOpplysninger)) {
            utdypendeOpplysninger = new ArrayList<>();
        }
        return utdypendeOpplysninger;
    }

    public Boolean getManglendeTilretteleggingPaaArbeidsplassen() {
        return manglendeTilretteleggingPaaArbeidsplassen != null && manglendeTilretteleggingPaaArbeidsplassen;
    }
}

