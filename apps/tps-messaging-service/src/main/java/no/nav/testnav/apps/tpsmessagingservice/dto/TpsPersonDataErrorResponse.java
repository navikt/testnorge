package no.nav.testnav.apps.tpsmessagingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "tpsPersonData")
public class TpsPersonDataErrorResponse {

    private TpsSvar tpsSvar;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TpsSvar {

        private TpsMeldingResponse svarStatus;
    }
}
