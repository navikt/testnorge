package no.nav.registre.aareg.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import no.nav.registre.aareg.consumer.rs.AaregstubConsumer;
import no.nav.registre.aareg.provider.rs.response.SletteArbeidsforholdResponse;

@Service
@RequiredArgsConstructor
public class IdentService {

    private final AaregstubConsumer aaregstubConsumer;

    public SletteArbeidsforholdResponse slettArbeidsforholdFraAaregstub(
            List<String> identer
    ) {
        var identerIAaregstub = aaregstubConsumer.hentEksisterendeIdenter();
        identer.retainAll(identerIAaregstub);

        return aaregstubConsumer.slettIdenterFraAaregstub(identer);
    }
}
