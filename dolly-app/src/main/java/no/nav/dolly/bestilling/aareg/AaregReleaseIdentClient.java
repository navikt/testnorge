package no.nav.dolly.bestilling.aareg;

import static java.math.BigDecimal.valueOf;
import static java.time.LocalDate.parse;
import static java.util.Arrays.asList;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.consumer.aareg.AaregRestConsumer;
import no.nav.dolly.consumer.aareg.AaregWsConsumer;
import no.nav.dolly.domain.resultset.aareg.RsAktoerPerson;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsavtale;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsforhold;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.domain.resultset.aareg.RsPeriode;
import no.nav.dolly.domain.resultset.aareg.RsPersonAareg;
import no.nav.dolly.domain.resultset.tpsf.EnvironmentsResponse;
import no.nav.dolly.exceptions.DollyFunctionalException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AaregReleaseIdentClient extends AaregAbstractClient {

    private final AaregWsConsumer aaregWsConsumer;
    private final AaregRestConsumer aaregRestConsumer;
    private final TpsfService tpsfService;

    public Map<String, String> deleteArbeidsforhold(String ident) {

        Map<String, String> resultMap = new HashMap<>();
        ResponseEntity<EnvironmentsResponse> environments = tpsfService.getEnvironments();

        if (environments.hasBody()) {
            environments.getBody().getEnvironments().forEach(environment -> {

                try {
                    ResponseEntity<Map[]> arbeidforhold = aaregRestConsumer.readArbeidsforhold(ident, environment);
                    if (arbeidforhold.hasBody()) {

                        asList(arbeidforhold.getBody()).forEach(
                                forhold -> {
                                    RsArbeidsforhold arbeidsforhold = RsArbeidsforhold.builder()
                                            .arbeidsforholdIDnav(getNavArbfholdId(forhold))
                                            .arbeidsforholdID(getArbforholdId(forhold))
                                            .arbeidsgiver("Person".equals(getArbeidsgiverType(forhold)) ?
                                                    RsAktoerPerson.builder()
                                                            .ident(getPersonnummer(forhold))
                                                            .build() :
                                                    RsOrganisasjon.builder()
                                                            .orgnummer(getOrgnummer(forhold))
                                                            .build())
                                            .arbeidsforholdstype(getArbeidsforholdType(forhold))
                                            .arbeidstaker(RsPersonAareg.builder()
                                                    .ident(getOffentligIdent(forhold))
                                                    .build())
                                            .ansettelsesPeriode(RsPeriode.builder()
                                                    .fom(parse(getPeriodeFom(forhold)).atStartOfDay())
                                                    .tom(parse(getPeriodeFom(forhold)).atStartOfDay())
                                                    .build())
                                            .arbeidsavtale(RsArbeidsavtale.builder()
                                                    .yrke(getYrkeskode(forhold))
                                                    .stillingsprosent(valueOf(0))
                                                    .endringsdatoStillingsprosent(parse(getPeriodeFom(forhold)).atStartOfDay())
                                                    .build())
                                            .build();
                                    resultMap.putAll(aaregWsConsumer.oppdaterArbeidsforhold(buildRequest(arbeidsforhold, environment)));
                                }
                        );
                    }
                } catch (HttpClientErrorException e) {
                    if (HttpStatus.NOT_FOUND.value() != e.getStatusCode().value()) {
                        throw e;
                    }
                } catch (DollyFunctionalException e) {
                    if (!e.getMessage().contains("Ugyldig miljø")) {
                        throw e;
                    }
                }
            });
        }
        return resultMap;
    }
}
