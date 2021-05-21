package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.amelding;

import java.time.LocalDate;
import java.util.List;

import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.VirksomhetDTO;

public class Opplysningspliktig {
    private LocalDate kalendermaaned;
    private String opplysningspliktigOrganisajonsnummer;
    private List<VirksomhetDTO> virksomheter;
}
