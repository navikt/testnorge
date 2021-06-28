package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdlBestillingResponse {

    private String hendelseId;

    private Map<String, String> deletedOpplysninger;
    private String feilmelding;
    private String message;
    private List<Object> details;

//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class ErrorDetails {
//
//        private Map<String, String> detail;
//    }
}
