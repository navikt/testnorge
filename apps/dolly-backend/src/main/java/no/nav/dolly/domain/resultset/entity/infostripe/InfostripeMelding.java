package no.nav.dolly.domain.resultset.entity.infostripe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfostripeMelding {

    private Long id;
    private InfoStripeType type;
    private String message;
    private LocalDateTime start;
    private LocalDateTime expires;
}
