package no.nav.registre.inntekt.provider.rs.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.inntekt.domain.dokmot.AvsenderMottaker;
import no.nav.registre.inntekt.domain.dokmot.Bruker;
import no.nav.registre.inntekt.domain.dokmot.Dokument;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DokmotRequest {
    @JsonProperty
    private String journalposttype;
    @JsonProperty
    private AvsenderMottaker avsenderMottaker;
    @JsonProperty
    private Bruker bruker;
    @JsonProperty
    private String tema;
    @JsonProperty
    private String tittel;
    @JsonProperty
    private String kanal;
    @JsonProperty
    private String eksternReferanseId;
    @JsonProperty
    private Date datoMottatt;
    @JsonProperty
    private List<Dokument> dokumenter;

    public void setDatoMottatt(Date datoMottatt) {
        this.datoMottatt = datoMottatt != null ? new Date(datoMottatt.getTime()) : null;
    }

    public Date getDatoMottatt() {
        return datoMottatt != null ? new Date(datoMottatt.getTime()) : null;
    }
}
