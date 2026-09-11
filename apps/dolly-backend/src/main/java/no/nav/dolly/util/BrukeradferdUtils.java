package no.nav.dolly.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.resultset.RsDollyBestilling;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@UtilityClass
public class BrukeradferdUtils {

    private static final Set<String> EXCLUDE_METHODS = Set.of("getClass", "getMalBestillingNavn", "getEnvironments", "getId");

    public static Map<String, Integer> getAntallAdferd(RsDollyBestilling bestilling, Integer antall) {

        var adferd = new HashMap<String, Integer>();
        Arrays.stream(bestilling.getClass().getMethods())
                .filter(metode -> metode.getName().startsWith("get"))
                .filter(metode -> !EXCLUDE_METHODS.contains(metode.getName()))
                .forEach(metode -> {
                    var system = metode.getName().substring(3);
                    try {

                        var verdi = metode.invoke(bestilling);

                        if (metode.getReturnType().equals(List.class) && !((List) verdi).isEmpty()
                            || !metode.getReturnType().equals(List.class) && nonNull(verdi)) {

                            adferd.merge(decodeData(system, bestilling), antall, Integer::sum);
                        }

                    } catch (IllegalAccessException | InvocationTargetException e) {
                        log.error("Feil ved henting av adferd: {}", e.getMessage(), e);
                        throw new RuntimeException(e);
                    }
                });
        return adferd;
    }

    private static String decodeData(String system, RsDollyBestilling bestilling) {

        return switch (system) {
            case "Pdldata" -> decodePdl(bestilling);
            case "Pensjonforvalter" -> decodePensjon(bestilling);
            case "Arenaforvalter" -> decodeArena(bestilling);
            case "Aareg" -> decodeAareg(bestilling);
            case "Fullmakt", "Instdata", "SigrunstubPensjonsgivende",
                 "SigrunstubSummertSkattegrunnlag", "Dokarkiv",
                 "Yrkesskader","EtterlatteYtelser" -> decodeAntall(bestilling, system);
            case "Bankkonto" -> decodeBankkonto(bestilling);
            default -> system;
        };
    }

    private static String decodeBankkonto(RsDollyBestilling bestilling) {

        var builder = new StringBuilder("Bankkonto=");

        var bankkonto = bestilling.getBankkonto();
        if (nonNull(bankkonto)) {
            if (nonNull(bankkonto.getNorskBankkonto())) {
                builder.append(",NorskBankkonto:true");
            }
            if (nonNull(bankkonto.getUtenlandskBankkonto())){
                builder.append(",UtenlandskBankkonto:true");
            }
        }

        return builder.toString();
    }

    private static String decodeAntall(RsDollyBestilling bestilling, String system) {

        var builder = new StringBuilder("%s=".formatted(system));

        try {
            var register = (List) bestilling.getClass().getMethod("get%s".formatted(system))
                    .invoke(bestilling);

            builder.append(",Array/matrise antall:")
                    .append(register.size());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("Feil ved henting av antall: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }

        return builder.toString();
    }

    private static String decodeAareg(RsDollyBestilling bestilling) {

        var builder = new StringBuilder("Aareg=");

        var aaregdata = bestilling.getAareg();
        builder.append(",Antall arbeidsforhold:")
                .append(aaregdata.size());

        return builder.toString();
    }

    private static String decodeArena(RsDollyBestilling bestilling) {

        var builder = new StringBuilder("Arena=");

        var arenadata = bestilling.getArenaforvalter();
        if (nonNull(arenadata)) {
            if (!arenadata.getAap().isEmpty()) {
                builder.append(",AAP:true");
            }
            if (!arenadata.getAap115().isEmpty()) {
                builder.append(",AAP115:true");
            }
            if (!arenadata.getDagpenger().isEmpty()) {
                builder.append(",Dagpenger:true");
            }
            if (nonNull(arenadata.getArenaBrukertype())) {
                builder.append(",ArenaBrukertype:")
                        .append(arenadata.getArenaBrukertype());
            }
            if (nonNull(arenadata.getKvalifiseringsgruppe())) {
                builder.append(",Kvalifiseringsgruppe:")
                        .append(arenadata.getKvalifiseringsgruppe());
            }
        }
        return builder.toString();
    }

    private static String decodePensjon(RsDollyBestilling bestilling) {

        var builder = new StringBuilder("Pensjon=");

        var pensjonsdata = bestilling.getPensjonforvalter();
        if (nonNull(pensjonsdata)) {
            if (nonNull(pensjonsdata.getInntekt())) {
                builder.append(",PoppInntekt:true");
            }
            if (nonNull(pensjonsdata.getGenerertInntekt())) {
                builder.append(",PoppSpesifisertInntekt:true");
            }
            if (nonNull(pensjonsdata.getAlderspensjon())) {
                builder.append(",Alderspensjon:true");
            }
            if (nonNull(pensjonsdata.getUforetrygd())) {
                builder.append(",Uforetrygd:true");
            }
            if (!pensjonsdata.getPensjonsavtale().isEmpty()) {
                builder.append(",Pensjonsavtale:")
                        .append(pensjonsdata.getPensjonsavtale().size());
            }
            if (!pensjonsdata.getTp().isEmpty()) {
                builder.append(",Tjenestepensjon:")
                        .append(pensjonsdata.getTp().size());
            }
            if (nonNull(pensjonsdata.getAfpOffentlig())) {
                builder.append(",AfpOffentlig:true");
            }
        }
        return builder.toString();
    }

    private static String decodePdl(RsDollyBestilling bestilling) {

        var builder = new StringBuilder("PdlData=");

        var pdldata = bestilling.getPdldata();
        if (nonNull(pdldata.getOpprettNyPerson())) {
            if (isTrue(pdldata.getOpprettNyPerson().getId2032())) {
                builder.append(",Id2032:true");
            } else {
                builder.append(",Syntetisk:")
                        .append(isTrue(pdldata.getOpprettNyPerson().getSyntetisk()));
            }
            if (nonNull(pdldata.getOpprettNyPerson().getAlder())) {
                builder.append(",Alder:true");
            }
            if (nonNull(pdldata.getOpprettNyPerson().getFoedtEtter())) {
                builder.append(",FødtEtter:true");
            }
            if (nonNull(pdldata.getOpprettNyPerson().getFoedtFoer())) {
                builder.append(",FødtFør:true");
            }
        } else {
            builder.append(",Legg-til/endre:true");
        }
        if (nonNull(pdldata.getPerson())) {
            Arrays.stream(pdldata.getPerson().getClass().getMethods())
                    .filter(metode -> metode.getName().startsWith("get"))
                    .filter(metode -> metode.getReturnType().equals(List.class))
                    .forEach(metode -> {
                        var opplysning = metode.getName().substring(3);
                        try {
                            var verdi = (List) metode.invoke(pdldata.getPerson());
                            if (!verdi.isEmpty()) {

                                builder.append(",")
                                        .append(opplysning)
                                        .append(':')
                                        .append(verdi.size());
                            }
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            log.error("Feil ved henting av adferd: {}", e.getMessage(), e);
                            throw new RuntimeException(e);
                        }
                    });
        }
        return builder.toString();
    }
}
