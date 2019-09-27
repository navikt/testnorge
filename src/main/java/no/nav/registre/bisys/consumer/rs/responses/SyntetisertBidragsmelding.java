package no.nav.registre.bisys.consumer.rs.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
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
public class SyntetisertBidragsmelding {

    private static final Integer DEFAULT_BA_ALDER = -1;

    @JsonProperty("BA")
    private String barn;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Integer barnAlderIMnd;

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

    @JsonProperty("GODKJENT_BELOP")
    private int godkjentBelop;

    @JsonProperty("INNBETALT")
    private String innbetalt;

    @JsonProperty("KRAVBELOP")
    private int kravbelop;

    @JsonProperty("MOTTATT_DATO")
    private String mottattDato;

    @JsonProperty("SOKNADSTYPE")
    private String soknadstype;

    @JsonProperty("SOKNAD_FRA")
    private String soknadFra;

    /**
     * @deprecated
     * 
     *             Not required once new dataset is ready (started processing 30.09.2019).
     */
    @Deprecated
    @JsonProperty("SOKT_FRA")
    @Getter(AccessLevel.NONE)
    private String soktFra;

    @JsonProperty("SOKT_FRA_I_MND_FOER_MOTTATT")
    private int soktFraIMndFoerMottattDato;

    @JsonProperty("SOKT_OM")
    private String soktOm;

    @JsonProperty(value = "BA_ALDER")
    public int getBarnAlderIMnd() {
        return barnAlderIMnd == null ? DEFAULT_BA_ALDER : barnAlderIMnd;
    }

    @JsonProperty(value = "BA_ALDER")
    public void setBarnAlderIMnd(Integer barnAlderIMnd) {
        this.barnAlderIMnd = barnAlderIMnd == null ? DEFAULT_BA_ALDER : barnAlderIMnd;
    }

}
