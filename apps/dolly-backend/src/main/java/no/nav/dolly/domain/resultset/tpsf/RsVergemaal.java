package no.nav.dolly.domain.resultset.tpsf;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsVergemaal {

    private Long id;

    private String embete;

    private String sakType;

    private LocalDateTime vedtakDato;

    private RsSimplePerson verge;

    private String mandatType;
}
