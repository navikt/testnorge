package no.nav.testnav.apps.tpsmessagingservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.tps.xjc.ctg.domain.s018.S018PersonType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@XmlRootElement(name = "tpsPersonData")
@XmlType(propOrder = {"tpsServiceRutine", "tpsSvar"})
public class TpsServicerutineS018Response {

    private TpsServicerutineAksjonsdatoRequest.TpsServiceRutineMedAksjonsdato tpsServiceRutine;
    private TpsServicerutineS018Response.TpsSvar tpsSvar;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)

    @XmlType(name = "TpsSvarType",
            propOrder = {"svarStatus", "ingenReturData", "personDataS018"})
    public static class TpsSvar {

        private TpsMeldingResponse svarStatus;
        private IngenReturData ingenReturData;
        private S018PersonType persondataS018;
    }

    public static class IngenReturData {
        public IngenReturData() {
        }
    }
}
