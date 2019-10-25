package no.nav.registre.aareg.service;

import static java.time.LocalDate.parse;
import static java.util.Arrays.asList;
import static no.nav.registre.aareg.service.AaregAbstractClient.buildRequest;
import static no.nav.registre.aareg.service.AaregAbstractClient.getArbeidsforholdType;
import static no.nav.registre.aareg.service.AaregAbstractClient.getArbeidsgiverType;
import static no.nav.registre.aareg.service.AaregAbstractClient.getArbforholdId;
import static no.nav.registre.aareg.service.AaregAbstractClient.getNavArbfholdId;
import static no.nav.registre.aareg.service.AaregAbstractClient.getOffentligIdent;
import static no.nav.registre.aareg.service.AaregAbstractClient.getOrgnummer;
import static no.nav.registre.aareg.service.AaregAbstractClient.getPeriodeFom;
import static no.nav.registre.aareg.service.AaregAbstractClient.getPersonnummer;
import static no.nav.registre.aareg.service.AaregAbstractClient.getYrkeskode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;

import no.nav.registre.aareg.consumer.rs.AaregRestConsumer;
import no.nav.registre.aareg.consumer.rs.TpsfConsumer;
import no.nav.registre.aareg.consumer.rs.responses.MiljoerResponse;
import no.nav.registre.aareg.consumer.ws.AaregWsConsumer;
import no.nav.registre.aareg.consumer.ws.request.RsAaregOppdaterRequest;
import no.nav.registre.aareg.consumer.ws.request.RsAaregOpprettRequest;
import no.nav.registre.aareg.domain.AnsettelsesPeriode;
import no.nav.registre.aareg.domain.Arbeidsavtale;
import no.nav.registre.aareg.domain.Arbeidsforhold;
import no.nav.registre.aareg.domain.RsAktoerPerson;
import no.nav.registre.aareg.domain.RsOrganisasjon;
import no.nav.registre.aareg.domain.RsPersonAareg;
import no.nav.registre.aareg.exception.TestnorgeAaregFunctionalException;
import no.nav.registre.aareg.provider.rs.response.RsAaregResponse;

@Service
public class AaregService {

    @Autowired
    private AaregWsConsumer aaregWsConsumer;

    @Autowired
    private AaregRestConsumer aaregRestConsumer;

    @Autowired
    private TpsfConsumer tpsfConsumer;

    public RsAaregResponse opprettArbeidsforhold(
            RsAaregOpprettRequest request
    ) {
        return aaregWsConsumer.opprettArbeidsforhold(request);
    }

    public Map<String, String> oppdaterArbeidsforhold(
            RsAaregOppdaterRequest request
    ) {
        return aaregWsConsumer.oppdaterArbeidsforhold(request);
    }

    public ResponseEntity hentArbeidsforhold(
            String ident,
            String miljoe
    ) {
        return aaregRestConsumer.hentArbeidsforhold(ident, miljoe);
    }

    public Map<String, String> slettArbeidsforhold(
            String ident
    ) {
        Map<String, String> resultMap = new HashMap<>();
        ResponseEntity<MiljoerResponse> miljoer = tpsfConsumer.hentMiljoer();

        if (miljoer.hasBody()) {
            miljoer.getBody().getMiljoer().forEach(environment -> {

                try {
                    ResponseEntity<Map[]> arbeidforhold = aaregRestConsumer.hentArbeidsforhold(ident, environment);
                    if (arbeidforhold.hasBody()) {

                        asList(arbeidforhold.getBody()).forEach(
                                forhold -> {
                                    Arbeidsforhold arbeidsforhold = Arbeidsforhold.builder()
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
                                            .ansettelsesPeriode(AnsettelsesPeriode.builder()
                                                    .fom(parse(getPeriodeFom(forhold)).atStartOfDay())
                                                    .tom(parse(getPeriodeFom(forhold)).atStartOfDay())
                                                    .build())
                                            .arbeidsavtale(Arbeidsavtale.builder()
                                                    .yrke(getYrkeskode(forhold))
                                                    .stillingsprosent(0.0)
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
                } catch (TestnorgeAaregFunctionalException e) {
                    if (!e.getMessage().contains("Ugyldig miljø")) {
                        throw e;
                    }
                }
            });
        }
        return resultMap;
    }
}
