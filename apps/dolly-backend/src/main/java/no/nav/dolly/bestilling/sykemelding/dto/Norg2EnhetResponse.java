package no.nav.dolly.bestilling.sykemelding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Norg2EnhetResponse {

    private String aktiveringsdato;
    private Integer antallRessurser;
    private Integer enhetId;
    private String enhetNr;
    private String kanalstrategi;
    private String navn;
    private String nedleggelsesdato;
    private Boolean oppgavebehandler;
    private String orgNivaa;
    private String orgNrTilKommunaltNavKontor;
    private String organisasjonsnummer;
    private String sosialeTjenester;
    private String status;
    private String type;
    private String underAvviklingDato;
    private String underEtableringDato;
    private Integer versjon;

    private HttpStatus httpStatus;
    private String avvik;
}
