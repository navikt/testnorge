package no.nav.testnav.apps.tpsmessagingservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "sfePersonData")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TpsServicerutineRequest {

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
