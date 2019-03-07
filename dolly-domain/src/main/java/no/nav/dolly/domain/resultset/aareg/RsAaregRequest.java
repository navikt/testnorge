package no.nav.dolly.domain.resultset.aareg;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsAaregRequest {

    private List<String> environments;
    private RsArbeidsforhold arbeidsforhold;
    private LocalDateTime rapporteringsperiode;

    public List<String> getEnvironments() {
        return environments;
    }
}
