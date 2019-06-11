package no.nav.registre.skd.service;

import static no.nav.registre.skd.service.utilities.RedigereSkdmeldingerUtility.opprettStatsborgerendringsmelding;
import static no.nav.registre.skd.service.utilities.RedigereSkdmeldingerUtility.putFnrInnIMelding;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import no.nav.registre.skd.consumer.HodejegerenConsumer;
import no.nav.registre.skd.consumer.IdentPoolConsumer;
import no.nav.registre.skd.consumer.requests.HentIdenterRequest;
import no.nav.registre.skd.skdmelding.RsMeldingstype;
import no.nav.registre.skd.skdmelding.RsMeldingstype1Felter;

/**
 * Service for å plassere nye identer i skdmeldingene.
 */
@Slf4j
@Service
public class NyeIdenterService {

    @Autowired
    private IdentPoolConsumer identPoolConsumer;

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    public List<String> settInnNyeIdenterITrans1Meldinger(String miljoe, HentIdenterRequest.IdentType identType, List<RsMeldingstype> meldinger) {
        int antallNyeIdenter = meldinger.size();
        HentIdenterRequest request = HentIdenterRequest.builder()
                .antall(antallNyeIdenter)
                .identtype(identType)
                .foedtEtter(LocalDate.now().minusYears(90)).build();
        List<String> identer = identPoolConsumer.hentNyeIdenter(request);
        for (int i = 0; i < antallNyeIdenter; i++) {
            String ident = identer.get(i);
            Map<String, String> statusQuoFraEndringskode = hodejegerenConsumer.getStatusQuoFraEndringskode(Endringskoder.UREGISTRERT_PERSON, miljoe, ident);
            if (statusQuoFraEndringskode.isEmpty()) {
                putFnrInnIMelding((RsMeldingstype1Felter) meldinger.get(i), identer.get(i));
                if (Endringskoder.INNVANDRING.getAarsakskode().equals(meldinger.get(i).getAarsakskode())) {
                    meldinger.add(opprettStatsborgerendringsmelding((RsMeldingstype1Felter) meldinger.get(i)));
                }
            } else {
                log.error("Ident {} eksisterte allerede i miljø. Hopper over opprettelse", ident);
            }
        }
        return identer;
    }
}
