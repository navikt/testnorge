package no.nav.registre.syntrest.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AAP115Melding {
    // legge til dato
    @JsonProperty("18-67AAR")
    String attenTilSekstisyvAar;
    @JsonProperty
    String AAARBEVNE;
    @JsonProperty
    String AAMOTTSAMT;
    @JsonProperty
    String AANAAVJOBB;
    @JsonProperty
    String AANODVDOKU;
    @JsonProperty
    String AANORSKFER;
    @JsonProperty
    String AASNARARBG;
    @JsonProperty
    String AASNARTARB;
    @JsonProperty
    String AATYPEJOBB;
    @JsonProperty
    String INNTNEDS;
    @JsonProperty
    String SYKSKADLYT;
    @JsonProperty
    String UTFALL;
    @JsonProperty
    String VEDTAK_ID; // ikke nødvwndig
    // Generelle saksopplysninger -- kode.verdi, KRAV(DATO) BTID(DATO)
    // disse datoene må ha sammenheng med fraDato og tilDato
}
