package no.nav.dolly.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.aareg.AaregClient;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.BestilteKriterier;
import no.nav.dolly.domain.resultset.IdentType;
import no.nav.dolly.domain.resultset.SystemTyper;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype;
import org.apache.commons.lang3.StringUtils;

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
import static no.nav.dolly.domain.resultset.SystemTyper.HENVENDELSE;
import static no.nav.dolly.domain.resultset.SystemTyper.HISTARK;
import static no.nav.dolly.domain.resultset.SystemTyper.INNTK;
import static no.nav.dolly.domain.resultset.SystemTyper.INNTKMELD;
import static no.nav.dolly.domain.resultset.SystemTyper.INST2;
import static no.nav.dolly.domain.resultset.SystemTyper.KELVIN_AAP;
import static no.nav.dolly.domain.resultset.SystemTyper.KONTOREGISTER;
import static no.nav.dolly.domain.resultset.SystemTyper.KRRSTUB;
import static no.nav.dolly.domain.resultset.SystemTyper.MEDL;
import static no.nav.dolly.domain.resultset.SystemTyper.NOM;
import static no.nav.dolly.domain.resultset.SystemTyper.OPPFOELGINGSVEDTAK14A;
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
import static no.nav.dolly.domain.resultset.SystemTyper.SIGRUN_PENSJONSGIVENDE;
import static no.nav.dolly.domain.resultset.SystemTyper.SIGRUN_SUMMERT;
import static no.nav.dolly.domain.resultset.SystemTyper.SKATTEKORT;
import static no.nav.dolly.domain.resultset.SystemTyper.SKJERMINGSREGISTER;
import static no.nav.dolly.domain.resultset.SystemTyper.SYKEMELDING;
import static no.nav.dolly.domain.resultset.SystemTyper.TPS_MESSAGING;
import static no.nav.dolly.domain.resultset.SystemTyper.TP_FORVALTER;
import static no.nav.dolly.domain.resultset.SystemTyper.UDISTUB;
import static no.nav.dolly.domain.resultset.SystemTyper.YRKESSKADE;
import static no.nav.dolly.util.TestnorgeIdentUtility.isTestnorgeIdent;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNumeric;

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
        var miljoer = getMiljoer(bestilling);

        utledPdlTyper(result, kriterier, bestilling);
        utledPensjonTyper(result, kriterier, bestilling, miljoer);
        utledFagsystemTyper(result, kriterier, miljoer);

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
                    } catch (IllegalArgumentException _) {
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

        result.add(PDL_PERSONSTATUS);

        if (isNotBlank(bestilling.getPdlImport())) {
            result.add(PDLIMPORT);
        }

        if (isGjenopprett(bestilling)) {
            if (!isTestnorgeIdent(bestilling.getGjenopprettetFraIdent())) {
                result.add(PDL_ORDRE);
            }

        } else if (isOpprettNyPerson(kriterier, bestilling) || harPerson(kriterier)) {
            result.add(PDL_FORVALTER);
            result.add(PDL_ORDRE);
        }
    }

    private boolean isOpprettNyPerson(BestilteKriterier kriterier, Bestilling bestilling) {

        return isBlank(bestilling.getIdent()) &&
                isBlank(bestilling.getPdlImport()) &&
                (isNotBlank(bestilling.getOpprettFraIdenter()) ||
                        nonNull(kriterier.getPdldata()) && nonNull(kriterier.getPdldata().getOpprettNyPerson()));
    }

    private boolean harPerson(BestilteKriterier kriterier) {

        return nonNull(kriterier.getPdldata()) && nonNull(kriterier.getPdldata().getPerson());
    }

    private void utledPensjonTyper(Set<SystemTyper> result, BestilteKriterier kriterier, Bestilling bestilling,
                                   Set<String> miljoer) {

        if (isNpid(kriterier, bestilling)) {
            return;
        }

        result.add(PEN_FORVALTER);

        var pensjon = kriterier.getPensjonforvalter();
        if (nonNull(pensjon) && !miljoer.isEmpty()) {
            if (pensjon.hasInntekt() || pensjon.hasGenerertInntekt()) result.add(PEN_INNTEKT);
            if (harTpYtelse(pensjon)) result.add(TP_FORVALTER);
            if (pensjon.hasAlderspensjon()) result.add(PEN_AP);
            if (pensjon.hasNyUttaksgrad() && !isGjenopprett(bestilling)) result.add(PEN_AP_NY_UTTAKSGRAD);
            if (pensjon.hasUforetrygd()) result.add(PEN_UT);
            if (pensjon.hasPensjonsavtale()) result.add(PEN_PENSJONSAVTALE);
            if (pensjon.hasAfpOffentlig()) result.add(PEN_AFP_OFFENTLIG);
        }
    }

    private boolean harTpYtelse(PensjonData pensjon) {

        return pensjon.getTp().stream()
                .anyMatch(tp -> !tp.getYtelser().isEmpty());
    }

    private boolean isNpid(BestilteKriterier kriterier, Bestilling bestilling) {

        if (isNotBlank(bestilling.getIdent())) {
            return isNpidIdent(bestilling.getIdent());
        }
        if (isNotBlank(bestilling.getGjenopprettetFraIdent())) {
            return isNpidIdent(bestilling.getGjenopprettetFraIdent());
        }
        if (isNotBlank(bestilling.getOpprettFraIdenter())) {
            return isAlleNpid(bestilling.getOpprettFraIdenter());
        }
        if (isNotBlank(bestilling.getPdlImport())) {
            return isAlleNpid(bestilling.getPdlImport());
        }
        return nonNull(kriterier.getPdldata()) &&
                nonNull(kriterier.getPdldata().getOpprettNyPerson()) &&
                kriterier.getPdldata().getOpprettNyPerson().getIdenttype() == Identtype.NPID;
    }

    private boolean isAlleNpid(String identer) {

        return Arrays.stream(identer.split(","))
                .map(String::trim)
                .allMatch(UtledFagsystemUtil::isNpidIdent);
    }

    private boolean isNpidIdent(String ident) {

        return ident.length() == 11 && isNumeric(ident) &&
                IdentTypeUtil.getIdentType(ident) == IdentType.NPID;
    }

    private void utledFagsystemTyper(Set<SystemTyper> result, BestilteKriterier kriterier, Set<String> miljoer) {

        if (harArbeidsforhold(kriterier) && miljoer.stream().anyMatch(AaregClient.MILJOER_SUPPORTED::contains))
            result.add(AAREG);
        if (nonNull(kriterier.getKrrstub())) result.add(KRRSTUB);
        if (nonNull(kriterier.getFullmakt()) && !kriterier.getFullmakt().isEmpty()) result.add(FULLMAKT);
        if (nonNull(kriterier.getMedl())) result.add(MEDL);
        if (nonNull(kriterier.getUdistub())) result.add(UDISTUB);
        if (nonNull(kriterier.getInstdata()) && !kriterier.getInstdata().isEmpty() && !miljoer.isEmpty())
            result.add(INST2);
        if (harInntektstub(kriterier)) result.add(INNTK);
        if (nonNull(kriterier.getArenaforvalter()) && !miljoer.isEmpty()) result.add(ARENA_BRUKER);
        if (nonNull(kriterier.getInntektsmelding()) && !miljoer.isEmpty()) result.add(INNTKMELD);
        if (nonNull(kriterier.getBrregstub())) result.add(BRREGSTUB);
        if (nonNull(kriterier.getDokarkiv()) && !kriterier.getDokarkiv().isEmpty() && !miljoer.isEmpty())
            result.add(DOKARKIV);
        if (nonNull(kriterier.getHistark()) && !kriterier.getHistark().getDokumenter().isEmpty()) result.add(HISTARK);
        if (nonNull(kriterier.getSykemelding()) && kriterier.getSykemelding().hasNySykemelding())
            result.add(SYKEMELDING);
        if (nonNull(kriterier.getBankkonto()) && (harBankkonto(kriterier) || harTpsMessagingBankkonto(kriterier)))
            result.add(KONTOREGISTER);
        if (nonNull(kriterier.getArbeidsplassenCV())) result.add(ARBEIDSPLASSENCV);
        if (nonNull(kriterier.getSkattekort()) && !kriterier.getSkattekort().getArbeidsgiverSkatt().isEmpty())
            result.add(SKATTEKORT);
        if (nonNull(kriterier.getYrkesskader()) && !kriterier.getYrkesskader().isEmpty()) result.add(YRKESSKADE);
        if (nonNull(kriterier.getNomdata())) result.add(NOM);
        if (nonNull(kriterier.getArbeidssoekerregisteret())) result.add(ARBEIDSSOEKERREGISTERET);
        if (nonNull(kriterier.getEtterlatteYtelser()) && !kriterier.getEtterlatteYtelser().isEmpty())
            result.add(ETTERLATTE);
        if (nonNull(kriterier.getKelvinAap())) result.add(KELVIN_AAP);
        if (nonNull(kriterier.getSigrunstubPensjonsgivende()) && !kriterier.getSigrunstubPensjonsgivende().isEmpty())
            result.add(SIGRUN_PENSJONSGIVENDE);
        if (nonNull(kriterier.getSigrunstubSummertSkattegrunnlag()) && !kriterier.getSigrunstubSummertSkattegrunnlag().isEmpty())
            result.add(SIGRUN_SUMMERT);
        if (harSkjerming(kriterier) || harTpsMessagingEgenansatt(kriterier)) result.add(SKJERMINGSREGISTER);
        if (harBankkonto(kriterier) || harSkjerming(kriterier) || harTpsMessagingEgenansatt(kriterier))
            result.add(TPS_MESSAGING);
        if (nonNull(kriterier.getOppfoelgingsvedtak14a())) result.add(OPPFOELGINGSVEDTAK14A);
        if (harHenvendelse(kriterier)) result.add(HENVENDELSE);
    }

    private Set<String> getMiljoer(Bestilling bestilling) {

        return isBlank(bestilling.getMiljoer()) ? Set.of() :
                Arrays.stream(bestilling.getMiljoer().split(","))
                        .map(String::trim)
                        .filter(StringUtils::isNotBlank)
                        .collect(Collectors.toSet());
    }

    private boolean harArbeidsforhold(BestilteKriterier kriterier) {

        return nonNull(kriterier.getAareg()) &&
                kriterier.getAareg().stream()
                        .anyMatch(aareg -> nonNull(aareg.getArbeidsgiver()));
    }

    private boolean harInntektstub(BestilteKriterier kriterier) {

        return nonNull(kriterier.getInntektstub()) && !kriterier.getInntektstub().getInntektsinformasjon().isEmpty() ||
                nonNull(kriterier.getInntekter()) && !kriterier.getInntekter().isEmpty();
    }

    private boolean harHenvendelse(BestilteKriterier kriterier) {

        return nonNull(kriterier.getHenvendelse()) &&
                (!kriterier.getHenvendelse().getMeldinger().isEmpty() ||
                        !kriterier.getHenvendelse().getSamtalereferater().isEmpty());
    }

    private boolean harBankkonto(BestilteKriterier kriterier) {

        return nonNull(kriterier.getBankkonto()) &&
                (nonNull(kriterier.getBankkonto().getNorskBankkonto()) ||
                        nonNull(kriterier.getBankkonto().getUtenlandskBankkonto()));
    }

    private boolean harTpsMessagingBankkonto(BestilteKriterier kriterier) {

        return nonNull(kriterier.getTpsMessaging()) &&
                (nonNull(kriterier.getTpsMessaging().getNorskBankkonto()) ||
                        nonNull(kriterier.getTpsMessaging().getUtenlandskBankkonto()));
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
