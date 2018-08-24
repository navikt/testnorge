package no.nav.dolly.domain.resultSet;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RsBestilling {

    private Long id;
    private int antallIdenter;
    private boolean ferdig;
    private LocalDateTime sistOppdatert;
    private long gruppeId;

    private List<String> environments;
    private List<RsBestillingProgress> personStatus = new ArrayList<>();
}
