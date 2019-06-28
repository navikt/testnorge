package no.nav.dolly.norg2;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Norg2EnhetResponse {

    private String enhetId;
    private String navn;
    private String enhetNr;
    private String antallRessurser;
    private String status;
    private String orgNivaa;
    private String type;
    private String organisasjonsnummer;
    private LocalDate underEtableringDato;
    private LocalDate aktiveringsdato;
    private LocalDate underAvviklingDato;
    private LocalDate nedleggelsesdato;
    private String oppgavebehandler;
    private String versjon;
    private String sosialeTjenester;
    private String kanalstrategi;
    private String orgNrTilKommunaltNavKontor;
}
