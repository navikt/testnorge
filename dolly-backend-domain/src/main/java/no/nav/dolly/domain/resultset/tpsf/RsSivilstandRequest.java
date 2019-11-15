package no.nav.dolly.domain.resultset.tpsf;

import java.time.LocalDateTime;

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
public class RsSivilstandRequest {

    private String sivilstand;
    private LocalDateTime sivilstandRegdato;
}
