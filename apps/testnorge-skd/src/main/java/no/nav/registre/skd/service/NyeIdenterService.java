package no.nav.registre.skd.service;

import static no.nav.registre.skd.service.utilities.RedigereSkdmeldingerUtility.opprettStatsborgerendringsmelding;
import static no.nav.registre.skd.service.utilities.RedigereSkdmeldingerUtility.putFnrInnIMelding;
import static no.nav.registre.skd.service.utilities.RedigereSkdmeldingerUtility.korrigerUtenFastBosted;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import no.nav.registre.skd.consumer.IdentPoolConsumer;
import no.nav.registre.skd.consumer.requests.HentIdenterRequest;
import no.nav.registre.skd.domain.Endringskoder;
import no.nav.registre.skd.skdmelding.RsMeldingstype;
import no.nav.registre.skd.skdmelding.RsMeldingstype1Felter;

/**
 * Service for å plassere nye identer i skdmeldingene.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NyeIdenterService {

    private final IdentPoolConsumer identPoolConsumer;

    public List<String> settInnNyeIdenterITrans1Meldinger(HentIdenterRequest.IdentType identType, List<RsMeldingstype> meldinger) {
        var antallNyeIdenter = meldinger.size();
        var request = HentIdenterRequest.builder()
                .antall(antallNyeIdenter)
                .identtype(identType)
                .foedtEtter(LocalDate.now().minusYears(90))
                .foedtFoer(LocalDate.now())
                .rekvirertAv("orkestratoren")
                .build();
        var identer = identPoolConsumer.hentNyeIdenter(request);
        for (int i = 0; i < antallNyeIdenter; i++) {
            putFnrInnIMelding((RsMeldingstype1Felter) meldinger.get(i), identer.get(i));
            if (Endringskoder.INNVANDRING.getAarsakskode().equals(meldinger.get(i).getAarsakskode())) {
                meldinger.add(opprettStatsborgerendringsmelding((RsMeldingstype1Felter) meldinger.get(i)));
            }
            if ("UTENFASTBOSTED".equals(((RsMeldingstype1Felter) meldinger.get(i)).getAdresse1())) {
                korrigerUtenFastBosted((RsMeldingstype1Felter) meldinger.get(i));
            }
        }
        return identer;
    }
}
