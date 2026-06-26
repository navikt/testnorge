package no.nav.dolly.domain.projection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OversiktFragment {

    private String maaned;
    private Long antall;
    private String gjenopprettstatus;
}
