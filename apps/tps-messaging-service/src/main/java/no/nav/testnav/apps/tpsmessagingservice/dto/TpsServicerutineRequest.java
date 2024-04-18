package no.nav.testnav.apps.tpsmessagingservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "tpsPersonData")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TpsServicerutineRequest implements TpsRequest {

    private TpsServiceRutine tpsServiceRutine;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlType(propOrder = {"serviceRutinenavn", "fnr", "aksjonsKode", "aksjonsKode2"})
    public static class TpsServiceRutine {

        private String serviceRutinenavn;
        private String fnr;
        private String aksjonsKode;
        private String aksjonsKode2;
    }
}
