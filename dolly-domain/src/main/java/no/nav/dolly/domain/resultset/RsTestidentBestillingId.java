package no.nav.dolly.domain.resultset;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsTestidentBestillingId extends RsTestident {

    private List<Long> bestillingId;
}
