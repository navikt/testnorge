package no.nav.dolly.domain.resultset.aareg;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RsAaregOppdaterRequest extends RsAaregOpprettRequest {

    private LocalDateTime rapporteringsperiode;
}
