package no.nav.testnav.levendearbeidsforholdansettelse.domain.tenor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenorOrganisasjonRequest {

    private Organisasjonsform organisasjonsform;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Organisasjonsform {

        private TenorOrganisasjonSelectOptions.OrganisasjonForm kode;
    }
}
