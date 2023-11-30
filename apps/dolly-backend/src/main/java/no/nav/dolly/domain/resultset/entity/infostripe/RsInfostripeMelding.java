package no.nav.dolly.domain.resultset.entity.infostripe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RsInfostripeMelding {

    private InfoStripeType type;
    private String message;
    private LocalDateTime start;
    private LocalDateTime expires;
}
