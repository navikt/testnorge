package no.nav.dolly.domain.projection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Bruker;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BestillingBrukerFragment {

    private Long id;
    private String bestKriterier;
    private String miljoer;
    private Bruker.Brukertype brukertype;
    private String brukerId;
    private LocalDate dato;
    private Integer antall;
    private String bestillingtype;

    private List<BestillingProgress> progresser;
    private Map<String, String> organisasjoner;
}