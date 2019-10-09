package no.nav.dolly.domain.resultset;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsTestidentBestillingId extends RsTestident {

    private boolean ibruk;
    private String beskrivelse;
    private List<Long> bestillingId;
}
