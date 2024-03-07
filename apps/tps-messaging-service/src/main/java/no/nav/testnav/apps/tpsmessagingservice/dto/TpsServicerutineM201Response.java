package no.nav.testnav.apps.tpsmessagingservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.tps.ctg.m201.domain.TpsServiceRutineType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TpsServicerutineM201Response {

    private TpsPersonData tpsPersonData;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class TpsPersonData {

        private TpsServiceRutineType tpsServiceRutine;
        private TpsSvar tpsSvar;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class TpsSvar {

        private TpsMeldingResponse svarStatus;
        private PersondataFraTpsM201 personDataM201;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class PersondataFraTpsM201 {

        private int antallFM201;
        private AFnr AFnr;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class AFnr {

        @JacksonXmlCData
        @JacksonXmlElementWrapper(useWrapping = false)
        private List<EFnr> EFnr;

        public List<EFnr> getEFnr() {
            if (Objects.isNull(EFnr)) {
                EFnr = new ArrayList<>();
            }
            return EFnr;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class EFnr {

        private String fnr;
        private String sp;
        private String kn;
        private String fn;
        private String mn;
        private String en;
        private TpsMeldingResponse svarStatus;
    }
}
