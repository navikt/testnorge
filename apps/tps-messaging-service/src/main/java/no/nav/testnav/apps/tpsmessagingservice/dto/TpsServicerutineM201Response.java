package no.nav.testnav.apps.tpsmessagingservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.tps.ctg.m201.domain.TpsServiceRutineType;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@XmlRootElement(name = "tpsPersonData")
@XmlType(propOrder = {"tpsServiceRutine", "tpsSvar"})
public class TpsServicerutineM201Response {

    private TpsServiceRutineType tpsServiceRutine;
    private TpsSvar tpsSvar;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @XmlType(propOrder = {"svarStatus", "personDataM201"})
    public static class TpsSvar {

        private TpsMeldingResponse svarStatus;
        private PersondataFraTpsM201 personDataM201;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @XmlType(propOrder = {"antallFM201", "AFnr"})
    public static class PersondataFraTpsM201 {

        private int antallFM201;
        private AFnr aFnr;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @XmlType(propOrder = "EFnr")
    public static class AFnr {

        @JacksonXmlCData
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "eFnr")
        private List<EFnr> eFnr;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @XmlType(propOrder = {"fnr", "sp", "kn", "fn", "mn", "en", "svarStatus"})
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
