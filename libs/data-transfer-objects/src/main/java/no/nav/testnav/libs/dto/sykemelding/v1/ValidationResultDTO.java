package no.nav.testnav.libs.dto.sykemelding.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResultDTO {

    private AllowedValues status;
    private List<Items> ruleHits;

    public List<Items> getRuleHits() {

        if (isNull(ruleHits)) {
            ruleHits = new ArrayList<>();
        }
        return ruleHits;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Items {

        private String ruleName;
        private String messageForSender;
        private String messageForUser;
        private AllowedValues ruleStatus;
    }

    public enum AllowedValues {OK, MANUAL_PROCESSING, INVALID}
}
