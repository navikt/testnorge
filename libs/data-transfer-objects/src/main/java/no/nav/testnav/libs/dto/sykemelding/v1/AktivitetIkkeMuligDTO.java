package no.nav.testnav.libs.dto.sykemelding.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AktivitetIkkeMuligDTO {
    private MedisinskArsakDTO medisinskArsak;
    private ArbeidsrelatertArsakDTO arbeidsrelatertArsak;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MedisinskArsakDTO {
        private String beskrivelse;
        private List<String> arsak;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ArbeidsrelatertArsakDTO {
        private String beskrivelse;
        private List<String> arsak;
    }
}
