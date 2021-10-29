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
public class RsVergemaalRequest {

    private String embete;

    private String sakType;

    private String mandatType;

    private LocalDateTime vedtakDato;

    private String identType;

    private Boolean harMellomnavn;
}