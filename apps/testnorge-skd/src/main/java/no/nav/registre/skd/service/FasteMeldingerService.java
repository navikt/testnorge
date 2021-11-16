package no.nav.registre.skd.service;

import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static no.nav.registre.skd.service.utilities.RedigereSkdmeldingerUtility.opprettStatsborgerendringsmelding;
import static no.nav.registre.skd.service.utilities.RedigereSkdmeldingerUtility.setObligatoriskeFelt;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.skd.consumer.IdentPoolConsumer;
import no.nav.registre.skd.consumer.SyntTpsConsumer;
import no.nav.registre.skd.consumer.TpsfConsumer;
import no.nav.registre.skd.consumer.requests.SendToTpsRequest;
import no.nav.registre.skd.consumer.response.SkdMeldingerTilTpsRespons;
import no.nav.registre.skd.domain.Endringskoder;
import no.nav.registre.skd.provider.rs.requests.FastMeldingRequest;
import no.nav.registre.skd.skdmelding.RsMeldingstype;
import no.nav.registre.skd.skdmelding.RsMeldingstype1Felter;

@Slf4j
@Service
@RequiredArgsConstructor
public class FasteMeldingerService {

    private final TpsfConsumer tpsfConsumer;

    private final SyntTpsConsumer syntTpsConsumer;

    private final IdentPoolConsumer identPoolConsumer;

    private final ValidationService validationService;

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
            List<FastMeldingRequest> fasteMeldinger,
            Boolean opprettEndringStatsborgerskap
    ) {
        var syntetiserteSkdmeldinger = syntTpsConsumer.getSyntetiserteSkdmeldinger(Endringskoder.INNVANDRING.getEndringskode(), fasteMeldinger.size());
        validationService.logAndRemoveInvalidMessages(syntetiserteSkdmeldinger, Endringskoder.INNVANDRING);

        if (syntetiserteSkdmeldinger.size() < fasteMeldinger.size()) {
            log.info("Mottok ikke riktig antall gyldige meldinger fra tpsSyntetisereren. Antall meldinger mottatt: {}. Antall meldinger forespurt: {}",
                    syntetiserteSkdmeldinger.size(), fasteMeldinger.size());
        }

        List<RsMeldingstype> nyeFasteMeldinger = new ArrayList<>(fasteMeldinger.size());

        for (int i = 0; i < fasteMeldinger.size() && i < syntetiserteSkdmeldinger.size(); i++) {
            var syntetisertMelding = (RsMeldingstype1Felter) syntetiserteSkdmeldinger.get(i);
            var fastMelding = fasteMeldinger.get(i);

            String adresselinje3 = opprettAdresselinje3(syntetisertMelding, fastMelding);

            var nyMelding = RsMeldingstype1Felter.builder()
                    .fodselsdato(fastMelding.getFoedselsdato())
                    .personnummer(fastMelding.getPersonnummer())
                    .fornavn(fastMelding.getFornavn())
                    .slektsnavn(fastMelding.getEtternavn())
                    .postnummer(fastMelding.getPostnr() != null ? fastMelding.getPostnr() : syntetisertMelding.getPostnummer())
                    .adressenavn(fastMelding.getAdresse() != null ? fastMelding.getAdresse() : syntetisertMelding.getAdressenavn())
                    .adresse3(adresselinje3)
                    .build();

            oprettNyMeldingFornavnSlektsnavn(nyMelding);

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

            if (isTrue(opprettEndringStatsborgerskap)) {
                nyeFasteMeldinger.add(opprettStatsborgerendringsmelding(nyMelding));
            }
        }

        return tpsfConsumer.saveSkdEndringsmeldingerInTPSF(avspillergruppeId, nyeFasteMeldinger);
    }

    private String opprettAdresselinje3(RsMeldingstype1Felter syntetisertMelding, FastMeldingRequest fastMelding) {
        String adresselinje3;
        if (fastMelding.getPostnr() != null && fastMelding.getBy() != null) {
            adresselinje3 = fastMelding.getPostnr() + " " + fastMelding.getBy();
        } else if (fastMelding.getPostnr() != null && fastMelding.getBy() == null) {
            adresselinje3 = "";
        } else {
            adresselinje3 = syntetisertMelding.getPostnummer() + " " + syntetisertMelding.getAdresse3();
        }
        adresselinje3 += fastMelding.getPostnr() != null ? fastMelding.getPostnr() : syntetisertMelding.getPostnummer();
        adresselinje3 += fastMelding.getBy() != null ? fastMelding.getBy() : syntetisertMelding.getAdresse3();
        return adresselinje3;
    }

    private void oprettNyMeldingFornavnSlektsnavn(RsMeldingstype1Felter nyMelding) {
        if (isNullOrEmpty(nyMelding.getSlektsnavn()) || isNullOrEmpty(nyMelding.getFornavn())) {
            var navn = identPoolConsumer.hentNavn();
            if (isNullOrEmpty(nyMelding.getFornavn())) {
                nyMelding.setFornavn(navn.getFornavn());
            }
            if (isNullOrEmpty(nyMelding.getSlektsnavn())) {
                nyMelding.setSlektsnavn(navn.getEtternavn());
            }
        }
    }

    private static boolean isNullOrEmpty(String s) {
        if (s == null) {
            return true;
        }
        return s.isEmpty();
    }
}
