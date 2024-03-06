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
public class TpsServicerutineAksjonsdatoRequest implements TpsRequest {

    private TpsServiceRutineMedAksjonsdato tpsServiceRutine;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlType(propOrder = {"serviceRutinenavn", "fnr", "aksjonsDato", "aksjonsKode", "aksjonsKode2", "buffNr"})
    public static class TpsServiceRutineMedAksjonsdato {

        private String serviceRutinenavn;
        private String fnr;
        private String aksjonsDato;
        private String aksjonsKode;
        private String aksjonsKode2;
        private String buffNr;
    }
}
