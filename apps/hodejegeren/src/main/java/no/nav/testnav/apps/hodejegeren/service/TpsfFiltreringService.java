package no.nav.testnav.apps.hodejegeren.service;

import static no.nav.testnav.apps.hodejegeren.service.EksisterendeIdenterService.TRANSAKSJONSTYPE;
import static no.nav.testnav.apps.hodejegeren.service.Endringskoder.FOEDSELSMELDING;
import static no.nav.testnav.apps.hodejegeren.service.Endringskoder.FOEDSELSNUMMERKORREKSJON;
import static no.nav.testnav.apps.hodejegeren.service.Endringskoder.INNVANDRING;
import static no.nav.testnav.apps.hodejegeren.service.Endringskoder.TILDELING_DNUMMER;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.hodejegeren.consumer.TpsfConsumer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TpsfFiltreringService {

    private final TpsfConsumer tpsfConsumer;

    public List<String> finnAlleIdenter(Long gruppeId) {
        return new ArrayList<>(tpsfConsumer.getIdenterFiltrertPaaAarsakskode(
                gruppeId, Arrays.asList(
                        FOEDSELSMELDING.getAarsakskode(),
                        INNVANDRING.getAarsakskode(),
                        FOEDSELSNUMMERKORREKSJON.getAarsakskode(),
                        TILDELING_DNUMMER.getAarsakskode()),
                TRANSAKSJONSTYPE));
    }

    public List<String> finnLevendeIdenter(Long gruppeId) {
        var opprettedeIdenterITpsf = finnAlleIdenter(gruppeId);
        var doedeOgUtvandredeIdenter = finnDoedeOgUtvandredeIdenter(gruppeId);

        var levendeIdenterINorge = new ArrayList<>(opprettedeIdenterITpsf);
        levendeIdenterINorge.removeAll(doedeOgUtvandredeIdenter);

        return levendeIdenterINorge;
    }

    public List<String> finnDoedeOgUtvandredeIdenter(Long gruppeId) {
        return new ArrayList<>(tpsfConsumer.getIdenterFiltrertPaaAarsakskode(
                gruppeId, Arrays.asList(
                        Endringskoder.DOEDSMELDING.getAarsakskode(),
                        Endringskoder.UTVANDRING.getAarsakskode()),
                TRANSAKSJONSTYPE));
    }

    public List<String> finnGifteIdenter(Long gruppeId) {
        return new ArrayList<>(tpsfConsumer.getIdenterFiltrertPaaAarsakskode(
                gruppeId, Collections.singletonList(
                        Endringskoder.VIGSEL.getAarsakskode()),
                TRANSAKSJONSTYPE));
    }

    public List<String> finnFoedteIdenter(Long gruppeId) {
        return new ArrayList<>(tpsfConsumer.getIdenterFiltrertPaaAarsakskode(
                gruppeId, Collections.singletonList(
                        FOEDSELSMELDING.getAarsakskode()),
                TRANSAKSJONSTYPE));
    }
}
