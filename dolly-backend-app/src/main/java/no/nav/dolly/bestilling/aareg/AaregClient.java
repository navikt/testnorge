package no.nav.dolly.bestilling.aareg;

import static java.util.Collections.singletonList;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.aareg.domain.AaregOpprettRequest;
import no.nav.dolly.bestilling.aareg.domain.Arbeidsforhold;
import no.nav.dolly.consumer.aareg.AaregConsumer;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.aareg.RsAktoer;
import no.nav.dolly.domain.resultset.aareg.RsAktoerPerson;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.domain.resultset.aareg.RsPersonAareg;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.metrics.Timed;

@Slf4j
@Service
@RequiredArgsConstructor
public class AaregClient extends AaregAbstractClient implements ClientRegister {

    private final MapperFacade mapperFacade;
    private final AaregConsumer aaregConsumer;

    @Override
    @Timed(name = "providers", tags = { "operation", "gjenopprettAareg" })
    public void gjenopprett(RsDollyBestillingRequest bestilling, TpsPerson tpsPerson, BestillingProgress progress) {

        StringBuilder result = new StringBuilder();

        if (!bestilling.getAareg().isEmpty()) {

            bestilling.getEnvironments().forEach(env -> {

                ResponseEntity<Map[]> response = ResponseEntity.ok(new Map[] {});
                try {
                    response = aaregConsumer.hentArbeidsforhold(tpsPerson.getHovedperson(), env);
                } catch (RuntimeException e) {
                    log.error("Lesing av aareg i {} feilet, {}", env, e.getLocalizedMessage());
                }

                for (int i = 0; i < bestilling.getAareg().size(); i++) {
                    Arbeidsforhold arbfInput = mapperFacade.map(bestilling.getAareg().get(i), Arbeidsforhold.class);
                    arbfInput.setArbeidsforholdID(Integer.toString(i + 1));
                    arbfInput.setArbeidstaker(RsPersonAareg.builder().ident(tpsPerson.getHovedperson()).build());
                    setPermisjonId(arbfInput);

                    boolean found = false;
                    for (int j = 0; j < response.getBody().length; j++) {
                        Map arbfFraAareg = response.getBody()[j];

                        if ((isMatchArbgivOrgnummer(arbfInput.getArbeidsgiver(), getIdentifyingNumber(arbfFraAareg)) ||
                                isMatchArbgivPersonnummer(arbfInput.getArbeidsgiver(), getIdentifyingNumber(arbfFraAareg))) &&
                                arbfInput.getArbeidsforholdID().equals(getArbforholdId(arbfFraAareg))) {

                            arbfInput.setArbeidsforholdIDnav(getNavArbfholdId(arbfFraAareg));
                            appendResult(
                                    aaregConsumer.oppdaterArbeidsforhold(buildRequest(arbfInput, env)).getStatusPerMiljoe(),
                                    arbfInput.getArbeidsforholdID(),
                                    result);

                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        appendResult(aaregConsumer.opprettArbeidsforhold(AaregOpprettRequest.builder()
                                .arbeidsforhold(arbfInput)
                                .environments(singletonList(env))
                                .build()).getStatusPerMiljoe(), arbfInput.getArbeidsforholdID(), result);
                    }
                }
            });
        }

        progress.setAaregStatus(result.length() > 1 ? result.substring(1) : null);
    }

    private void setPermisjonId(Arbeidsforhold arbfInput) {
        if (!arbfInput.getPermisjon().isEmpty()) {
            arbfInput.getPermisjon().sort(Comparator.comparing(perm -> perm.getPermisjonsPeriode().getFom()));
            AtomicInteger id = new AtomicInteger(1);
            arbfInput.getPermisjon().forEach(perm -> perm.setPermisjonsId(Integer.toString(id.getAndAdd(1))));
        }
    }

    @Override
    public void release(List<String> identer) {
        identer.forEach(aaregConsumer::slettArbeidsforholdFraAlleMiljoer);
    }

    private static String getIdentifyingNumber(Map arbfFraAareg) {
        return "Organisasjon".equals(getArbeidsgiverType(arbfFraAareg)) ? getOrgnummer(arbfFraAareg) : getPersonnummer(arbfFraAareg);
    }

    private static boolean isMatchArbgivOrgnummer(RsAktoer arbeidsgiver, String orgnummer) {
        return arbeidsgiver instanceof RsOrganisasjon &&
                ((RsOrganisasjon) arbeidsgiver).getOrgnummer().equals(orgnummer);
    }

    private static boolean isMatchArbgivPersonnummer(RsAktoer arbeidsgiver, String ident) {
        return arbeidsgiver instanceof RsAktoerPerson &&
                ((RsAktoerPerson) arbeidsgiver).getIdent().equals(ident);
    }

    private static StringBuilder appendResult(Map<String, String> result, String arbeidsforholdId, StringBuilder builder) {
        for (Map.Entry<String, String> entry : result.entrySet()) {
            builder.append(',')
                    .append(entry.getKey())
                    .append(": arbforhold=")
                    .append(arbeidsforholdId)
                    .append('$')
                    .append(entry.getValue().replaceAll(",", "&").replaceAll(":", "="));
        }
        return builder;
    }
}