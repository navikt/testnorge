package no.nav.appserivces.tpsf.domain.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RsBestilling {

    private Long id;
    private int antallIdenter;
    private boolean ferdig;
    private LocalDate sistOppdatert;
    private long gruppeId;

    private List<String> environments;
    private List<RsBestillingProgress> personStatus = new ArrayList<>();
}
