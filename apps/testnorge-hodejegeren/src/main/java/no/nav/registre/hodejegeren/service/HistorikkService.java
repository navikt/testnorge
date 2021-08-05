package no.nav.registre.hodejegeren.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.hodejegeren.logging.Level;
import no.nav.registre.hodejegeren.logging.LogEvent;
import no.nav.registre.hodejegeren.logging.LogEventDTO;
import no.nav.registre.hodejegeren.logging.LogService;
import no.nav.registre.hodejegeren.mongodb.Data;
import no.nav.registre.hodejegeren.mongodb.Kilde;
import no.nav.registre.hodejegeren.mongodb.SyntHistorikk;
import no.nav.registre.hodejegeren.mongodb.SyntHistorikkRepository;
import no.nav.registre.hodejegeren.provider.rs.requests.DataRequest;
import no.nav.registre.hodejegeren.provider.rs.requests.HistorikkRequest;
import no.nav.registre.hodejegeren.provider.rs.requests.skd.PersonDokumentWrapper;
import no.nav.registre.hodejegeren.service.utilities.PersonDokumentUtility;
import no.nav.testnav.libs.domain.dto.namespacetps.TpsPersonDokumentType;

@RequiredArgsConstructor
@Service
public class HistorikkService {

    private static final int INCREASE_IN_NUMBER_OF_RESULTS = 100;
    private final SyntHistorikkRepository syntHistorikkRepository;
    private final MongoTemplate mongoTemplate;
    private final LogService logService;

    public List<SyntHistorikk> hentAllHistorikk(
            int pageNumber,
            int pageSize
    ) {
        var pageableRequest = PageRequest.of(pageNumber, pageSize);
        Page<SyntHistorikk> page = syntHistorikkRepository.findAll(pageableRequest);
        return page.getContent();
    }

    public SyntHistorikk hentHistorikkMedId(String id) {
        return syntHistorikkRepository.findById(id).orElse(null);
    }

    public List<SyntHistorikk> hentHistorikkMedKilder(
            List<String> kilder,
            int pageNumber,
            int pageSize
    ) {
        return syntHistorikkRepository.findAllByIdIn(new ArrayList<>(hentIdsMedKilder(kilder)), PageRequest.of(pageNumber, pageSize)).getContent();
    }

    public List<SyntHistorikk> hentHistorikkMedKriterier(
            List<String> kilder,
            int pageNumber,
            int pageSize,
            List<String> keywordList
    ) {
        var query = new Query();
        for (var keyword : keywordList) {
            var keyValue = keyword.split("=");
            // query.addCriteria(Criteria.where("kilder.data.innhold." + keyValue[0]).is(Pattern.compile(keyValue[1], Pattern.CASE_INSENSITIVE))); // finner også substrings. Sikkerhet?
            query.addCriteria(Criteria.where("kilder.data.innhold." + keyValue[0]).is(keyValue[1]));
            logService.log(new LogEvent(LogEventDTO.builder()
                    .level(Level.INFO)
                    .message("testnorge-hodejegeren-search - search for key: " + keyValue[0])
                    .key(keyValue[0])
                    .build()));

        }
        query.limit(pageSize * INCREASE_IN_NUMBER_OF_RESULTS);
        query.skip((long) pageNumber * pageSize);


        List<SyntHistorikk> results = new ArrayList<>(mongoTemplate.find(query, SyntHistorikk.class));
        results.forEach(historikk -> historikk.getKilder().removeIf(kilde -> !kilder.contains(kilde.getNavn())));

        Collections.shuffle(results);
        return results.stream().limit(pageSize).collect(Collectors.toList());
    }

    public Set<String> hentIdsMedKilder(List<String> kilder) {
        Set<String> idsMedAlleKilder = new HashSet<>();
        for (String kilde : kilder) {
            var historikkByKildenavn = syntHistorikkRepository.findAllIdsByKildenavn(kilde);
            Set<String> ids = new HashSet<>(historikkByKildenavn.size());
            for (var historikk : historikkByKildenavn) {
                ids.add(historikk.getId());
            }
            if (idsMedAlleKilder.isEmpty()) {
                idsMedAlleKilder.addAll(ids);
            } else {
                idsMedAlleKilder.retainAll(ids);
            }
        }
        return idsMedAlleKilder;
    }

    public SyntHistorikk opprettHistorikk(SyntHistorikk syntHistorikk) {
        var kilder = syntHistorikk.getKilder();
        for (var kilde : kilder) {
            for (var d : kilde.getData()) {
                if (d.getDatoOpprettet() == null) {
                    d.setDatoOpprettet(LocalDateTime.now());
                }
                if (d.getDatoEndret() == null) {
                    d.setDatoEndret(d.getDatoOpprettet());
                }
            }
        }
        return syntHistorikkRepository.save(syntHistorikk);
    }

    public List<String> leggTilHistorikkPaaIdent(HistorikkRequest historikkRequest) {
        List<String> opprettedeIder = new ArrayList<>();
        var kilder = historikkRequest.getIdentMedData();

        for (var kilde : kilder) {
            var id = kilde.getId();
            List<Data> nyData = new ArrayList<>(kilde.getData().size());

            leggTilDataFraKilde(kilde, nyData);

            var eksisterendeHistorikk = hentHistorikkMedId(id);
            if (eksisterendeHistorikk != null) {
                var eksisterendeKilder = eksisterendeHistorikk.getKilder();
                List<Data> eksisterendeData = null;
                for (var k : eksisterendeKilder) {
                    if (historikkRequest.getKilde().equals(k.getNavn())) {
                        eksisterendeData = k.getData();
                        break;
                    }
                }

                if (eksisterendeData != null) {
                    eksisterendeData.addAll(nyData);
                } else {
                    eksisterendeKilder.add(Kilde.builder().navn(historikkRequest.getKilde()).data(nyData).build());
                }

                opprettedeIder.add(syntHistorikkRepository.save(eksisterendeHistorikk).getId());
            } else {
                var nyKilde = Kilde.builder().navn(historikkRequest.getKilde()).data(nyData).build();
                opprettedeIder.add(opprettHistorikk(SyntHistorikk.builder().id(id).kilder(Collections.singletonList(nyKilde)).build()).getId());
            }
        }
        return opprettedeIder;
    }

    private void leggTilDataFraKilde(DataRequest kilde, List<Data> nyData) {
        var opprettelsesTidspunkt = LocalDateTime.now();
        for (var o : kilde.getData()) {
            nyData.add(Data.builder()
                    .innhold(o)
                    .datoOpprettet(opprettelsesTidspunkt)
                    .datoEndret(opprettelsesTidspunkt)
                    .build());
        }
    }

    public List<String> oppdaterTpsPersonDokument(
            String ident,
            TpsPersonDokumentType tpsPersonDokument
    ) {
        var syntHistorikk = hentHistorikkMedId(ident);
        var personDokumentWrapper = PersonDokumentUtility.convertToPersonDokumentWrapper(tpsPersonDokument);
        if (syntHistorikk != null) {
            Kilde kilde = null;
            for (var k : syntHistorikk.getKilder()) {
                if ("skd".equals(k.getNavn())) {
                    kilde = k;
                    break;
                }
            }
            if (kilde != null) {
                var eksisterendeSkdData = kilde.getData().get(0);
                eksisterendeSkdData.setInnhold(personDokumentWrapper);
                eksisterendeSkdData.setDatoEndret(LocalDateTime.now());
                return Collections.singletonList(syntHistorikkRepository.save(syntHistorikk).getId());
            } else {
                return leggTilSkdData(ident, personDokumentWrapper);
            }
        } else {
            return leggTilSkdData(ident, personDokumentWrapper);
        }
    }

    public ResponseEntity<String> slettHistorikk(String id) {
        var historikk = syntHistorikkRepository.findById(id).orElse(null);
        if (historikk != null) {
            syntHistorikkRepository.deleteById(id);
            return ResponseEntity.ok(String.format("Historikk tilhørende id '%s' ble slettet", id));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Fant ingen historikk tilhørende id '%s'", id));
        }
    }

    public ResponseEntity<String> slettKilde(
            String id,
            String navnPaaKilde
    ) {
        var historikk = syntHistorikkRepository.findById(id).orElse(null);

        if (historikk != null) {
            List<Kilde> kilder = historikk.getKilder();
            Kilde kilde = null;
            for (Kilde k : kilder) {
                if (navnPaaKilde.equals(k.getNavn())) {
                    kilde = k;
                    break;
                }
            }
            if (kilde != null) {
                kilder.remove(kilde);
                if (historikk.getKilder().isEmpty()) {
                    slettHistorikk(id);
                } else {
                    syntHistorikkRepository.save(historikk);
                }
                return ResponseEntity.ok(String.format("Kilde '%s' tilhørende id '%s' ble slettet", navnPaaKilde, id));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Fant ingen identMedData med navn '%s' tilhørende id '%s'", navnPaaKilde, id));
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Fant ingen historikk tilhørende id '%s'", id));
        }
    }

    private List<String> leggTilSkdData(
            String ident,
            PersonDokumentWrapper personDokumentWrapper
    ) {
        var historikkRequest = HistorikkRequest.builder()
                .kilde("skd")
                .identMedData(Collections.singletonList(DataRequest.builder()
                        .id(ident)
                        .data(Collections.singletonList(personDokumentWrapper))
                        .build()))
                .build();
        return leggTilHistorikkPaaIdent(historikkRequest);
    }
}
