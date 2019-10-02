package no.nav.registre.arena.core.consumer.rs.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AAP115Melding {
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
    String VEDTAK_ID;
}
