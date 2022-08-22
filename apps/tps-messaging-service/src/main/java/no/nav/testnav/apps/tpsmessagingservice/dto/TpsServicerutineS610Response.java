package no.nav.testnav.apps.tpsmessagingservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.tps.ctg.s610.domain.PersondataFraTpsS610Type;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TpsServicerutineS610Response {

    private TpsPersonData tpsPersonData;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class TpsPersonData {

        private TpsServiceRutine tpsServiceRutine;
        private TpsSvar tpsSvar;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class TpsSvar {

        private TpsMeldingResponse svarStatus;
        private PersondataFraTpsS610Type personDataS610;
    }
}
