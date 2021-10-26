package no.nav.dolly.domain.resultset.tpsf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsFullmaktRequest {

    private String identType;
    private Boolean harMellomnavn;
    private LocalDateTime gyldigFom;
    private LocalDateTime gyldigTom;
    private String kilde;
    private List<String> omraader;
}
