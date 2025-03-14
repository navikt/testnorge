package no.nav.testnav.libs.dto.arena.testnorge.brukere;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Arbeidsoeker {

    private String personident;
    private String miljoe;
    private String status;
    private String eier;
    private Boolean servicebehov;
    private Boolean automatiskInnsendingAvMeldekort;
    private Boolean aap115;
    private Boolean aap;
    private String oppfolging;
}
