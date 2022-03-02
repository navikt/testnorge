package no.nav.registre.bisys.service;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.bidrag.ui.bisys.kodeverk.KodeSoknGrKomConstants;
import no.nav.registre.bisys.consumer.PersonSearchConsumer;
import no.nav.registre.bisys.consumer.request.FoedselSearch;
import no.nav.registre.bisys.consumer.request.PersonSearchRequest;
import no.nav.registre.bisys.consumer.request.RelasjonSearch;
import no.nav.registre.bisys.consumer.response.SyntetisertBidragsmelding;
import no.nav.registre.bisys.service.utils.Barn;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.testnorge.consumers.hodejegeren.responses.Relasjon;
import no.nav.registre.testnorge.consumers.hodejegeren.responses.RelasjonsResponse;

import no.nav.testnav.libs.dto.personsearchservice.v1.PersonDTO;
import org.joda.time.Months;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.joda.time.LocalDate;
//import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static no.nav.registre.bisys.service.utils.DateUtils.getAgeInMonths;
import static no.nav.registre.bisys.service.utils.DateUtils.getBirthdate;
import static no.nav.registre.bisys.service.SyntetiseringService.MIN_MOTTATT_DATO;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentService {

    public static final String RELASJON_MOR = "MOR";
    public static final String RELASJON_FAR = "FAR";
    public static final int MAX_AGE_BARN_AT_MOTTATTDATO = 20;
//    public static final LocalDate MIN_MOTTATT_DATO = new LocalDate(2007, 1, 1);

    private final Random rand = new Random();
    private final HodejegerenConsumer hodejegerenConsumer;
    private final PersonSearchConsumer personSearchConsumer;

    @Value("${USE_HISTORICAL_MOTTATTDATO}")
    private boolean useHistoricalMottattdato;

//    public Barn getValidId(SyntetisertBidragsmelding bidragsmelding){
//        boolean needYoungAdult = isSoktOm18(bidragsmelding.getSoktOm());
//        if (needYoungAdult){
//            return getYoungAdultWithForeldre(getBornAfterDateYoungAdult(), getBornBeforeDateYoungAdult());
//        }else {
//            return getBarnWithForeldre(getBornBeforeDate());
//        }
//    }
//
//    private boolean isSoktOm18(String soktOm) {
//        return KodeSoknGrKomConstants.BIDRAG_18_AAR.equals(soktOm) || KodeSoknGrKomConstants.BIDRAG_18_AAR_INNKREVING.equals(soktOm);
//    }
//
//    private LocalDate getBornBeforeDate(){
//        if (useHistoricalMottattdato) {
//            return MIN_MOTTATT_DATO.minusYears(MAX_AGE_BARN_AT_MOTTATTDATO);
//        } else {
//            return LocalDate.now().minusYears(MAX_AGE_BARN_AT_MOTTATTDATO);
//        }
//    }
//
//    private LocalDate getBornBeforeDateYoungAdult(){
//        // TODO
//        return null;
//    }
//
//    private LocalDate getBornAfterDateYoungAdult(){
//        // TODO
//        return null;
//    }
//
//    private Barn getBarnWithForeldre(LocalDate bornBefore){
//        var searchRequest = PersonSearchRequest.builder()
//                .page(1)
//                .pageSize(10)
//                .randomSeed(rand.nextFloat() + "")
//                .foedsel(FoedselSearch.builder()
//                        .tom(bornBefore.minusDays(1))
//                        .build())
//                .relasjoner(RelasjonSearch.builder()
//                        .mor(true)
//                        .far(true)
//                        .build())
//                .build();
//
//        var ident = personSearchConsumer.search(searchRequest).getItems().get(0);
//        return getBarn(personSearchConsumer.search(searchRequest).getItems().get(0));
//    }
//
//    private Barn getYoungAdultWithForeldre(LocalDate bornAfter, LocalDate bornBefore){
//        var searchRequest = PersonSearchRequest.builder()
//                .page(1)
//                .pageSize(10)
//                .randomSeed(rand.nextFloat() + "")
//                .foedsel(FoedselSearch.builder()
//                        .fom(bornAfter.plusDays(1))
//                        .tom(bornBefore.minusDays(1))
//                        .build())
//                .relasjoner(RelasjonSearch.builder()
//                        .mor(true)
//                        .far(true)
//                        .build())
//                .build();
//
//        return getBarn(personSearchConsumer.search(searchRequest).getItems().get(0));
//    }
//
//    private Barn getBarn(PersonDTO ident){
//        return Barn.builder()
//                .fnr(ident.getIdent())
//                .farFnr(ident.getRelasjoner().foreldre.stream()
//                        .filter(forelder -> forelder.rolle == RELASJON_FAR)
//                        .findFirst()
//                        .getIdent()
//                )
//                .morFnr(ident.getRelasjoner().foreldre.stream()
//                        .filter(forelder -> forelder.rolle == RELASJON_MOR)
//                        .findFirst()
//                        .getIdent())
//                .build();
//    }
//
    public List<Barn> selectValidUids(
            int antallIdenter, Long avspillergruppeId, String miljoe) {

        List<String> identerMedFoedselsmelding = finnFoedteIdenter(avspillergruppeId);
        List<Barn> utvalgteIdenter = new ArrayList<>();

        for (String ident : identerMedFoedselsmelding) {

            if (isBarnTooOld(ident)) {
                continue;
            }

            RelasjonsResponse relasjonsResponse = finnRelasjonerTilIdent(ident, miljoe);
            List<Relasjon> relasjoner = relasjonsResponse.getRelasjoner();

            String morFnr = "";
            String farFnr = "";

            for (Relasjon relasjon : relasjoner) {
                if (RELASJON_MOR.equals(relasjon.getTypeRelasjon())) {
                    morFnr = relasjon.getFnrRelasjon();
                } else if (RELASJON_FAR.equals(relasjon.getTypeRelasjon())) {
                    farFnr = relasjon.getFnrRelasjon();
                }
            }

            if (!morFnr.isEmpty() && !farFnr.isEmpty()) {
                utvalgteIdenter.add(Barn.builder().fnr(ident).morFnr(morFnr).farFnr(farFnr).build());
            }

            if (utvalgteIdenter.size() >= antallIdenter) {
                break;
            }
        }

        return utvalgteIdenter;
    }

    private boolean isBarnTooOld(String baFnr) {
        LocalDate birthdate = getBirthdate(baFnr);
        if (useHistoricalMottattdato) {
            return birthdate.isBefore(MIN_MOTTATT_DATO.minusYears(MAX_AGE_BARN_AT_MOTTATTDATO));
        } else {
            return birthdate.isBefore(LocalDate.now().minusYears(MAX_AGE_BARN_AT_MOTTATTDATO));
        }
    }

    @Timed(value = "bisys.resource.latency", extraTags = {"operation", "hodejegeren"})
    public RelasjonsResponse finnRelasjonerTilIdent(String ident, String miljoe) {
        return hodejegerenConsumer.getRelasjoner(ident, miljoe);
    }

    @Timed(value = "bisys.resource.latency", extraTags = {"operation", "hodejegeren"})
    private List<String> finnFoedteIdenter(Long avspillergruppeId) {
        return hodejegerenConsumer.getFoedte(avspillergruppeId);
    }

}
