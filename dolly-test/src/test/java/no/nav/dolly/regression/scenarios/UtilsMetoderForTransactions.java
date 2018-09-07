package no.nav.dolly.regression.scenarios;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.repository.TestGruppeRepository;

import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UtilsMetoderForTransactions {

    public Set<Bruker> getGruppeMedFavoritter(String navn, TestGruppeRepository repository){
        Testgruppe testgruppe = repository.findByNavn(navn);
        return testgruppe.getFavorisertAv();     // Metode for aa trigge henting av favoriset av.
    }
}
