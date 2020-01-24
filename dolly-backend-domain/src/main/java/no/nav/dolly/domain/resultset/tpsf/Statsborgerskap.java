package no.nav.dolly.domain.resultset.tpsf;

import java.time.LocalDateTime;
import javax.persistence.Entity;

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
public class Statsborgerskap {

    private Long id;
    private String statsborgerskap;
    private LocalDateTime statsborgerskapRegdato;
}