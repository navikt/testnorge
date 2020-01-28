package no.nav.registre.skd.service;

import static no.nav.registre.skd.service.utilities.RedigereSkdmeldingerUtility.setObligatoriskeFelt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.skd.consumer.IdentPoolConsumer;
import no.nav.registre.skd.consumer.TpsSyntetisererenConsumer;
import no.nav.registre.skd.consumer.TpsfConsumer;
import no.nav.registre.skd.consumer.requests.SendToTpsRequest;
import no.nav.registre.skd.consumer.response.SkdMeldingerTilTpsRespons;
import no.nav.registre.skd.provider.rs.requests.FastMeldingRequest;
import no.nav.registre.skd.skdmelding.RsMeldingstype;
import no.nav.registre.skd.skdmelding.RsMeldingstype1Felter;

@Service
@Slf4j
public class FasteMeldingerService {

    @Autowired
    private TpsfConsumer tpsfConsumer;

    @Autowired
    private TpsSyntetisererenConsumer tpsSyntetisererenConsumer;

    @Autowired
    private IdentPoolConsumer identPoolConsumer;

    @Autowired
    private ValidationService validationService;

    public SkdMeldingerTilTpsRespons startAvspillingAvTpsfAvspillergruppe(
            Long avspillergruppeId,
            String miljoe
    ) {
        var meldingIdsFromAvspillergruppe = tpsfConsumer.getMeldingIdsFromAvspillergruppe(avspillergruppeId);
        var sendToTpsRequest = new SendToTpsRequest(miljoe, meldingIdsFromAvspillergruppe);
        return tpsfConsumer.sendSkdmeldingerToTps(avspillergruppeId, sendToTpsRequest);
    }

    public List<Long> opprettMeldingerOgLeggIGruppe(
            Long avspillergruppeId,
            List<FastMeldingRequest> fasteMeldinger
    ) {
        var syntetiserteSkdmeldinger = tpsSyntetisererenConsumer.getSyntetiserteSkdmeldinger(Endringskoder.INNVANDRING.getEndringskode(), fasteMeldinger.size());
        validationService.logAndRemoveInvalidMessages(syntetiserteSkdmeldinger, Endringskoder.INNVANDRING);

        if (syntetiserteSkdmeldinger.size() < fasteMeldinger.size()) {
            log.info("Mottok ikke riktig antall gyldige meldinger fra tpsSyntetisereren. Antall meldinger mottatt: {}. Antall meldinger forespurt: {}",
                    syntetiserteSkdmeldinger.size(), fasteMeldinger.size());
        }

        List<RsMeldingstype> nyeFasteMeldinger = new ArrayList<>(fasteMeldinger.size());

        for (int i = 0; i < fasteMeldinger.size() && i < syntetiserteSkdmeldinger.size(); i++) {
            var syntetisertMelding = (RsMeldingstype1Felter) syntetiserteSkdmeldinger.get(i);
            var fastMelding = fasteMeldinger.get(i);

            var nyMelding = RsMeldingstype1Felter.builder()
                    .fodselsdato(fastMelding.getFoedselsdato())
                    .personnummer(fastMelding.getPersonnummer())
                    .fornavn(fastMelding.getFornavn())
                    .slektsnavn(fastMelding.getEtternavn())
                    .postnummer(fastMelding.getPostnr() != null ? fastMelding.getPostnr() : syntetisertMelding.getPostnummer())
                    .adressenavn(fastMelding.getAdresse() != null ? fastMelding.getAdresse() : syntetisertMelding.getAdressenavn())
                    .adresse3(fastMelding.getBy() != null ? fastMelding.getBy() : syntetisertMelding.getAdresse3())
                    .build();

            if (isNullOrEmpty(nyMelding.getSlektsnavn()) || isNullOrEmpty(nyMelding.getFornavn())) {
                var navn = identPoolConsumer.hentNavn();
                if (isNullOrEmpty(nyMelding.getFornavn())) {
                    nyMelding.setFornavn(navn.getFornavn());
                }
                if (isNullOrEmpty(nyMelding.getSlektsnavn())) {
                    nyMelding.setSlektsnavn(navn.getEtternavn());
                }
            }

            setObligatoriskeFelt(syntetisertMelding, nyMelding);
            nyMelding.setStatuskode(syntetisertMelding.getStatuskode());
            nyMelding.setTildelingskode(syntetisertMelding.getTildelingskode());
            nyMelding.setRegDato(syntetisertMelding.getRegDato());
            nyMelding.setRegdatoAdr(syntetisertMelding.getRegdatoAdr());
            nyMelding.setFlyttedatoAdr(syntetisertMelding.getFlyttedatoAdr());
            nyMelding.setFraLandRegdato(syntetisertMelding.getFraLandRegdato());
            nyMelding.setFraLandFlyttedato(syntetisertMelding.getFraLandFlyttedato());
            nyMelding.setInnvandretFraLand(syntetisertMelding.getInnvandretFraLand());
            nyMelding.setFraLandRegdato(syntetisertMelding.getFraLandRegdato());

            nyeFasteMeldinger.add(nyMelding);
        }

        return tpsfConsumer.saveSkdEndringsmeldingerInTPSF(avspillergruppeId, nyeFasteMeldinger);
    }

    private static boolean isNullOrEmpty(String s) {
        if (s == null) {
            return true;
        }
        return s.isEmpty();
    }
}
