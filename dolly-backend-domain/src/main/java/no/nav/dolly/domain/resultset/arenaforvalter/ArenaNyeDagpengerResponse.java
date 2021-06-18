package no.nav.dolly.domain.resultset.arenaforvalter;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArenaNyeDagpengerResponse {

    public enum DagpFeilstatus {DUPLIKAT, MILJOE_IKKE_STOETTET, AKTIVER_DAGP}

    private List<Dagp> nyeDagp;
    private List<NyDagpFeilV1> nyeDagpFeilList;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Dagp {

        private List<Vilkaar> vilkaar;
        private NyeDagpResponse nyeDagpResponse;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NyeDagpResponse {

        private String utfall;
        private String begrunnelse;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Vilkaar {

        private String kode;
        private String status;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NyDagpFeilV1 {

        private String personident;
        private String miljoe;
        private DagpFeilstatus nyDagpFeilstatus;
        private String melding;
    }

    public List<Dagp> getNyeDagp() {
        if (isNull(nyeDagp)) {
            nyeDagp = new ArrayList<>();
        }
        return nyeDagp;
    }

    public List<NyDagpFeilV1> getNyeDagpFeilList() {
        if (isNull(nyeDagpFeilList)) {
            nyeDagpFeilList = new ArrayList<>();
        }
        return nyeDagpFeilList;
    }
}
