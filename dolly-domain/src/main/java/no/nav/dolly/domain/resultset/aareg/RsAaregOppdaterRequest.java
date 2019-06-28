package no.nav.dolly.domain.resultset.aareg;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RsAaregOppdaterRequest extends RsAaregOpprettRequest {

    private LocalDateTime rapporteringsperiode;
}
