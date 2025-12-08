package no.nav.dolly.domain.resultset;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsNomData {

    private LocalDateTime startDato;
    private LocalDateTime sluttDato;
}
