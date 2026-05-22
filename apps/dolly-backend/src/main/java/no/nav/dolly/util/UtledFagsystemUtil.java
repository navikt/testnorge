package no.nav.dolly.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.BestilteKriterier;
import no.nav.dolly.domain.resultset.SystemTyper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.AAREG;
import static no.nav.dolly.domain.resultset.SystemTyper.ARBEIDSPLASSENCV;
import static no.nav.dolly.domain.resultset.SystemTyper.ARBEIDSSOEKERREGISTERET;
import static no.nav.dolly.domain.resultset.SystemTyper.ARENA_BRUKER;
import static no.nav.dolly.domain.resultset.SystemTyper.BRREGSTUB;
import static no.nav.dolly.domain.resultset.SystemTyper.DOKARKIV;
import static no.nav.dolly.domain.resultset.SystemTyper.ETTERLATTE;
import static no.nav.dolly.domain.resultset.SystemTyper.FULLMAKT;
import static no.nav.dolly.domain.resultset.SystemTyper.HISTARK;
import static no.nav.dolly.domain.resultset.SystemTyper.INNTK;
import static no.nav.dolly.domain.resultset.SystemTyper.INNTKMELD;
import static no.nav.dolly.domain.resultset.SystemTyper.INST2;
import static no.nav.dolly.domain.resultset.SystemTyper.KELVIN_AAP;
import static no.nav.dolly.domain.resultset.SystemTyper.KONTOREGISTER;
import static no.nav.dolly.domain.resultset.SystemTyper.KRRSTUB;
import static no.nav.dolly.domain.resultset.SystemTyper.MEDL;
import static no.nav.dolly.domain.resultset.SystemTyper.NOM;
import static no.nav.dolly.domain.resultset.SystemTyper.PDLIMPORT;
import static no.nav.dolly.domain.resultset.SystemTyper.PDL_FORVALTER;
import static no.nav.dolly.domain.resultset.SystemTyper.PDL_ORDRE;
import static no.nav.dolly.domain.resultset.SystemTyper.PDL_PERSONSTATUS;
import static no.nav.dolly.domain.resultset.SystemTyper.PEN_AFP_OFFENTLIG;
import static no.nav.dolly.domain.resultset.SystemTyper.PEN_AP;
import static no.nav.dolly.domain.resultset.SystemTyper.PEN_AP_NY_UTTAKSGRAD;
import static no.nav.dolly.domain.resultset.SystemTyper.PEN_FORVALTER;
import static no.nav.dolly.domain.resultset.SystemTyper.PEN_INNTEKT;
import static no.nav.dolly.domain.resultset.SystemTyper.PEN_PENSJONSAVTALE;
import static no.nav.dolly.domain.resultset.SystemTyper.PEN_UT;
import static no.nav.dolly.domain.resultset.SystemTyper.SIGRUN_LIGNET;
import static no.nav.dolly.domain.resultset.SystemTyper.SIGRUN_PENSJONSGIVENDE;
import static no.nav.dolly.domain.resultset.SystemTyper.SIGRUN_SUMMERT;
import static no.nav.dolly.domain.resultset.SystemTyper.SKATTEKORT;
import static no.nav.dolly.domain.resultset.SystemTyper.SKJERMINGSREGISTER;
import static no.nav.dolly.domain.resultset.SystemTyper.SYKEMELDING;
import static no.nav.dolly.domain.resultset.SystemTyper.TPS_MESSAGING;
import static no.nav.dolly.domain.resultset.SystemTyper.TP_FORVALTER;
import static no.nav.dolly.domain.resultset.SystemTyper.UDISTUB;
import static no.nav.dolly.domain.resultset.SystemTyper.YRKESSKADE;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@UtilityClass
public class UtledFagsystemUtil {

    private static final Map<SystemTyper, Integer> PRIORITY_ORDER = Map.of(
            PDL_FORVALTER, 0,
            PDLIMPORT, 0,
            PDL_ORDRE, 1,
            PDL_PERSONSTATUS, 2,
            PEN_FORVALTER, 3
    );

    public static List<SystemTyper> resolve(BestilteKriterier kriterier, Bestilling bestilling) {

        Set<SystemTyper> result = EnumSet.noneOf(SystemTyper.class);

        utledPdlTyper(result, kriterier, bestilling);
        utledPensjonTyper(result, kriterier);
        utledFagsystemTyper(result, kriterier);

        if (isGjenopprett(bestilling)) {
            result.remove(PDL_FORVALTER);
            result.add(PDLIMPORT);
            result.add(PDL_ORDRE);
            result.add(PDL_PERSONSTATUS);
        }

        return sortFagsystemer(new ArrayList<>(result));
    }

    public static String serialize(List<SystemTyper> fagsystemer) {

        if (fagsystemer == null || fagsystemer.isEmpty()) {
            return null;
        }
        return fagsystemer.stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));
    }

    public static List<SystemTyper> deserialize(String value) {

        if (isBlank(value)) {
            return List.of();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(name -> {
                    try {
                        return SystemTyper.valueOf(name);
                    } catch (IllegalArgumentException e) {
                        log.warn("Ukjent SystemTyper ved deserialisering: {}", name);
                        return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    private static boolean isGjenopprett(Bestilling bestilling) {

        return isNotBlank(bestilling.getGjenopprettetFraIdent()) ||
                nonNull(bestilling.getOpprettetFraId()) ||
                nonNull(bestilling.getOpprettetFraGruppeId());
    }

    public static <T> void sortStatusList(List<T> list, java.util.function.Function<T, SystemTyper> idExtractor) {

        list.sort(Comparator.<T, Integer>comparing(
                item -> PRIORITY_ORDER.getOrDefault(idExtractor.apply(item), Integer.MAX_VALUE)
        ).thenComparing(
                item -> {
                    var id = idExtractor.apply(item);
                    return nonNull(id) ? id.getBeskrivelse() : "";
                }
        ));
    }

    private List<SystemTyper> sortFagsystemer(List<SystemTyper> list) {

        list.sort(Comparator.<SystemTyper, Integer>comparing(
                type -> PRIORITY_ORDER.getOrDefault(type, Integer.MAX_VALUE)
        ).thenComparing(SystemTyper::getBeskrivelse));
        return list;
    }

    private void utledPdlTyper(Set<SystemTyper> result, BestilteKriterier kriterier, Bestilling bestilling) {

        if (isNotBlank(bestilling.getPdlImport())) {
            result.add(PDLIMPORT);
            result.add(PDL_FORVALTER);
            result.add(PDL_ORDRE);
        }
        if (nonNull(kriterier.getPdldata())) {
            result.add(PDL_FORVALTER);
            result.add(PDL_ORDRE);
            result.add(PDL_PERSONSTATUS);
        }
    }

    private void utledPensjonTyper(Set<SystemTyper> result, BestilteKriterier kriterier) {

        result.add(PEN_FORVALTER);

        var pensjon = kriterier.getPensjonforvalter();
        if (nonNull(pensjon)) {
            if (pensjon.hasInntekt() || pensjon.hasGenerertInntekt()) result.add(PEN_INNTEKT);
            if (pensjon.hasTp()) result.add(TP_FORVALTER);
            if (pensjon.hasAlderspensjon()) result.add(PEN_AP);
            if (pensjon.hasNyUttaksgrad()) result.add(PEN_AP_NY_UTTAKSGRAD);
            if (pensjon.hasUforetrygd()) result.add(PEN_UT);
            if (pensjon.hasPensjonsavtale()) result.add(PEN_PENSJONSAVTALE);
            if (pensjon.hasAfpOffentlig()) result.add(PEN_AFP_OFFENTLIG);
        }
    }

    private void utledFagsystemTyper(Set<SystemTyper> result, BestilteKriterier kriterier) {

        if (nonNull(kriterier.getAareg()) && !kriterier.getAareg().isEmpty()) result.add(AAREG);
        if (nonNull(kriterier.getKrrstub())) result.add(KRRSTUB);
        if (nonNull(kriterier.getFullmakt()) && !kriterier.getFullmakt().isEmpty()) result.add(FULLMAKT);
        if (nonNull(kriterier.getMedl())) result.add(MEDL);
        if (nonNull(kriterier.getUdistub())) result.add(UDISTUB);
        if (nonNull(kriterier.getInstdata()) && !kriterier.getInstdata().isEmpty()) result.add(INST2);
        if (nonNull(kriterier.getInntektstub())) result.add(INNTK);
        if (nonNull(kriterier.getArenaforvalter())) result.add(ARENA_BRUKER);
        if (nonNull(kriterier.getInntektsmelding())) result.add(INNTKMELD);
        if (nonNull(kriterier.getBrregstub())) result.add(BRREGSTUB);
        if (nonNull(kriterier.getDokarkiv()) && !kriterier.getDokarkiv().isEmpty()) result.add(DOKARKIV);
        if (nonNull(kriterier.getHistark())) result.add(HISTARK);
        if (nonNull(kriterier.getSykemelding())) result.add(SYKEMELDING);
        if (nonNull(kriterier.getBankkonto())) result.add(KONTOREGISTER);
        if (nonNull(kriterier.getArbeidsplassenCV())) result.add(ARBEIDSPLASSENCV);
        if (nonNull(kriterier.getSkattekort())) result.add(SKATTEKORT);
        if (nonNull(kriterier.getYrkesskader()) && !kriterier.getYrkesskader().isEmpty()) result.add(YRKESSKADE);
        if (nonNull(kriterier.getNomdata())) result.add(NOM);
        if (nonNull(kriterier.getArbeidssoekerregisteret())) result.add(ARBEIDSSOEKERREGISTERET);
        if (nonNull(kriterier.getEtterlatteYtelser()) && !kriterier.getEtterlatteYtelser().isEmpty())
            result.add(ETTERLATTE);
        if (nonNull(kriterier.getKelvinAap())) result.add(KELVIN_AAP);
        if (nonNull(kriterier.getSigrunstub()) && !kriterier.getSigrunstub().isEmpty()) result.add(SIGRUN_LIGNET);
        if (nonNull(kriterier.getSigrunstubPensjonsgivende()) && !kriterier.getSigrunstubPensjonsgivende().isEmpty())
            result.add(SIGRUN_PENSJONSGIVENDE);
        if (nonNull(kriterier.getSigrunstubSummertSkattegrunnlag()) && !kriterier.getSigrunstubSummertSkattegrunnlag().isEmpty())
            result.add(SIGRUN_SUMMERT);
        if (harSkjerming(kriterier) || harTpsMessagingEgenansatt(kriterier)) result.add(SKJERMINGSREGISTER);
        if (nonNull(kriterier.getBankkonto()) ||
                nonNull(kriterier.getSkjerming()) ||
                (nonNull(kriterier.getPdldata()) && nonNull(kriterier.getPdldata().getPerson())))
            result.add(TPS_MESSAGING);
    }

    private boolean harSkjerming(BestilteKriterier kriterier) {

        return nonNull(kriterier.getSkjerming()) &&
                (nonNull(kriterier.getSkjerming().getEgenAnsattDatoFom()) ||
                        nonNull(kriterier.getSkjerming().getEgenAnsattDatoTom()));
    }

    private boolean harTpsMessagingEgenansatt(BestilteKriterier kriterier) {

        return nonNull(kriterier.getTpsMessaging()) &&
                (nonNull(kriterier.getTpsMessaging().getEgenAnsattDatoFom()) ||
                        nonNull(kriterier.getTpsMessaging().getEgenAnsattDatoTom()));
    }
}
