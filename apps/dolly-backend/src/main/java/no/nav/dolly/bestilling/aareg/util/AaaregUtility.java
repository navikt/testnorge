package no.nav.dolly.bestilling.aareg.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdEksistens;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdRespons;
import no.nav.dolly.domain.resultset.BAFeilkoder;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.dto.aareg.v1.Organisasjon;
import no.nav.testnav.libs.dto.aareg.v1.Person;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;

@UtilityClass
@Slf4j
public class AaaregUtility {

    public static boolean isEqualArbeidsforhold(Arbeidsforhold response, Arbeidsforhold request) {

        return (isArbeidsgiverOrganisasjonAlike(response, request) ||
                isArbeidsgiverPersonAlike(response, request));
    }

    public static ArbeidsforholdEksistens doEksistenssjekk(ArbeidsforholdRespons response,
                                                           List<Arbeidsforhold> request) {

        return ArbeidsforholdEksistens.builder()
                .nyeArbeidsforhold(request.stream()
                        .filter(arbeidsforhold -> isFalse(arbeidsforhold.getIsOppdatering()) ||
                                isTrue(arbeidsforhold.getIsOppdatering()) &&
                                        response.getEksisterendeArbeidsforhold().stream()
                                                .noneMatch(response1 ->
                                                        isEqualArbeidsforhold(response1, arbeidsforhold)))
                        .toList())
                .eksisterendeArbeidsforhold(request.stream()
                        .filter(arbeidsforhold -> isTrue(arbeidsforhold.getIsOppdatering()) &&
                                response.getEksisterendeArbeidsforhold().stream()
                                        .anyMatch(response1 -> isEqualArbeidsforhold(response1, arbeidsforhold)))
                        .toList())
                .build();
    }

    public static void appendPermisjonPermitteringId(Arbeidsforhold arbeidsforhold, Arbeidsforhold eksisterende) {

        var permisjonPermitteringId = new AtomicInteger(isNull(eksisterende) ? 0 :
                eksisterende.getPermisjonPermitteringer().size());

        if (isNull(eksisterende)) {
            arbeidsforhold.getPermisjonPermitteringer().stream()
                    .filter(permisjon -> isBlank(permisjon.getPermisjonPermitteringId()))
                    .forEach(permisjon ->
                            permisjon.setPermisjonPermitteringId(Integer.toString(permisjonPermitteringId.incrementAndGet())));
        } else {
            arbeidsforhold.getPermisjonPermitteringer().stream()
                    .filter(permisjon -> isBlank(permisjon.getPermisjonPermitteringId()))
                    .forEach(permisjon -> permisjon.setPermisjonPermitteringId(eksisterende.getPermisjonPermitteringer().stream()
                            .anyMatch(eksist -> eksist.equals(permisjon)) ?
                            eksisterende.getPermisjonPermitteringer().stream()
                                    .filter(eksist -> eksist.equals(permisjon))
                                    .findFirst().get().getPermisjonPermitteringId() :
                            Integer.toString(permisjonPermitteringId.incrementAndGet())));
        }
    }

    public static StringBuilder appendResult(Map.Entry<String, String> entry, String
            arbeidsforholdId, StringBuilder builder) {
        return builder.append(',')
                .append(entry.getKey())
                .append(": arbforhold=")
                .append(arbeidsforholdId)
                .append('$')
                .append(ErrorStatusDecoder.encodeStatus(entry.getValue()));
    }

    public static String konverterBAfeilkodeTilFeilmelding(String baKode) {
        var baFeilkode = getBaFeilkodeFromFeilmelding(baKode);
        try {
            return baKode.replace(baFeilkode, BAFeilkoder.valueOf(baFeilkode).getBeskrivelse());
        } catch (IllegalArgumentException e) {
            log.warn("Mottok ukjent BA feilkode i feilmeldingen: {}", baKode);
            return baKode;
        }
    }

    private static String getBaFeilkodeFromFeilmelding(String baKode) {
        var setninger = baKode.split(" ");
        return Arrays.stream(setninger)
                .filter(ord -> ord.contains("BA"))
                .findFirst()
                .orElse("");
    }

    private static boolean isArbeidsgiverPersonAlike(Arbeidsforhold arbeidsforhold1, Arbeidsforhold arbeidsforhold2) {

        return arbeidsforhold1.getArbeidsgiver() instanceof Person person1 &&
                arbeidsforhold2.getArbeidsgiver() instanceof Person person2 &&
                person1.getOffentligIdent().equals(person2.getOffentligIdent());
    }

    private static boolean isArbeidsgiverOrganisasjonAlike(Arbeidsforhold arbeidsforhold1, Arbeidsforhold arbeidsforhold2) {

        return arbeidsforhold1.getArbeidsgiver() instanceof Organisasjon organisasjon1 &&
                arbeidsforhold2.getArbeidsgiver() instanceof Organisasjon organisasjon2 &&
                organisasjon1.getOrganisasjonsnummer().equals(organisasjon2.getOrganisasjonsnummer());
    }
}
