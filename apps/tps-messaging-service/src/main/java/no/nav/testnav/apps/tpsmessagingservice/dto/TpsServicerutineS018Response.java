package no.nav.testnav.apps.tpsmessagingservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class TpsServicerutineS018Response {

    private TpsPersonData tpsPersonData;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class TpsPersonData {
        private TpsServicerutineAksjonsdatoRequest.TpsServiceRutineMedAksjonsdato tpsServiceRutine;
        private TpsServicerutineS018Response.TpsSvar tpsSvar;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class TpsSvar {

        private TpsMeldingResponse svarStatus;
        private S018PersonType personDataS018;
    }
}
