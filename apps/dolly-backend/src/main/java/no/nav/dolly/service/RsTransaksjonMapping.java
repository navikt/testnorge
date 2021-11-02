package no.nav.dolly.service;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsTransaksjonMapping {

    private Long id;
    private Long bestillingId;
    private String ident;
    private String system;
    private String miljoe;
    private TransaksjonId transaksjonId;
    private LocalDateTime datoEndret;
    private String status;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TransaksjonId {

        private String journalpostId;
        private String dokumentInfoId;
        private String orgnummer;
        private String arbeidsforholdId;
    }
}
