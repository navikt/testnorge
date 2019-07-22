package no.nav.registre.aareg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import no.nav.registre.aareg.consumer.rs.AaregstubConsumer;
import no.nav.registre.aareg.provider.rs.responses.SletteArbeidsforholdResponse;

@Service
public class IdentService {

    @Autowired
    private AaregstubConsumer aaregstubConsumer;

    public SletteArbeidsforholdResponse slettArbeidsforholdFraAaregstub(List<String> identer) {
        List<String> identerIAaregstub = aaregstubConsumer.hentEksisterendeIdenter();
        identer.retainAll(identerIAaregstub);

        return aaregstubConsumer.slettIdenterFraAaregstub(identer);
    }
}
