package no.nav.registre.bisys.consumer.rs.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BidragsResponse {

    @JsonProperty("BA")
    private String barnetsFnr;

    @JsonProperty("BM")
    private String bidragsmottaker;

    @JsonProperty("BP")
    private String bidragspliktig;

    @JsonProperty("FASTSATT_I")
    private String fastsattI;

    @JsonProperty("GEBYRFRITAK_BM")
    private String gebyrfritakBm;

    @JsonProperty("GEBYRFRITAK_BP")
    private String gebyrfritakBp;

    @JsonProperty("INNBETALT")
    private String innbetalt;

    @JsonProperty("MOTTATT_DATO")
    private String mottattDato;

    @JsonProperty("SOKNADSTYPE")
    private String soknadstype;

    @JsonProperty("SOKNAD_FRA")
    private String soknadFra;

    @JsonProperty("SOKT_FRA")
    private String soktFra;

    @JsonProperty("SOKT_OM")
    private String soktOm;
}
