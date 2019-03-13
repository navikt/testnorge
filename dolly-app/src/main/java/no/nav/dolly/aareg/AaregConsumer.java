package no.nav.dolly.aareg;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.xml.datatype.XMLGregorianCalendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.resultset.aareg.RsAaregOppdaterRequest;
import no.nav.dolly.domain.resultset.aareg.RsAaregOpprettRequest;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.OppdaterArbeidsforholdArbeidsforholdIkkeFunnet;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.OppdaterArbeidsforholdSikkerhetsbegrensning;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.OppdaterArbeidsforholdUgyldigInput;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.OpprettArbeidsforholdSikkerhetsbegrensning;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.OpprettArbeidsforholdUgyldigInput;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.feil.ForretningsmessigUnntak;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.informasjon.Arbeidsforhold;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.meldinger.OppdaterArbeidsforholdRequest;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.meldinger.OpprettArbeidsforholdRequest;

@Service
@Slf4j
public class AaregConsumer {

    private static final String STATUS_OK = "OK";

    @Autowired
    private BehandleArbeidsforholdV1Proxy behandleArbeidsforholdV1Proxy;

    @Autowired
    private MapperFacade mapperFacade;

    public Map<String, String> opprettArbeidsforhold(RsAaregOpprettRequest request) {

        OpprettArbeidsforholdRequest arbeidsforholdRequest = new OpprettArbeidsforholdRequest();
        arbeidsforholdRequest.setArbeidsforhold(mapperFacade.map(request.getArbeidsforhold(), Arbeidsforhold.class));
        arbeidsforholdRequest.setArkivreferanse(getUuid(request.getArkivreferanse()));

        Map<String, String> status = new HashMap(request.getEnvironments().size());
        request.getEnvironments().forEach(env -> {
            try {
                behandleArbeidsforholdV1Proxy.getServiceByEnvironment(env).opprettArbeidsforhold(arbeidsforholdRequest);
                status.put(env, STATUS_OK);
            } catch (OpprettArbeidsforholdSikkerhetsbegrensning | OpprettArbeidsforholdUgyldigInput | DollyFunctionalException error) {
                status.put(env, extractError(error));
            }
        });

        return status;
    }

    public Map<String, String> oppdaterArbeidsforhold(RsAaregOppdaterRequest request) {

        OppdaterArbeidsforholdRequest arbeidsforholdRequest = new OppdaterArbeidsforholdRequest();
        arbeidsforholdRequest.setArbeidsforhold(mapperFacade.map(request.getArbeidsforhold(), Arbeidsforhold.class));
        arbeidsforholdRequest.setArkivreferanse(getUuid(request.getArkivreferanse()));
        arbeidsforholdRequest.setRapporteringsperiode(mapperFacade.map(request.getRapporteringsperiode(), XMLGregorianCalendar.class));

        Map<String, String> status = new HashMap(request.getEnvironments().size());
        request.getEnvironments().forEach(env -> {
            try {
                behandleArbeidsforholdV1Proxy.getServiceByEnvironment(env).oppdaterArbeidsforhold(arbeidsforholdRequest);
                status.put(env, STATUS_OK);
            } catch (OppdaterArbeidsforholdArbeidsforholdIkkeFunnet | OppdaterArbeidsforholdSikkerhetsbegrensning | OppdaterArbeidsforholdUgyldigInput | DollyFunctionalException error) {
                status.put(env, extractError(error));
            }
        });

        return status;
    }

    private String extractError(Exception exception) {

        String feilbeskrivelse = "";
        try {
            Method method = exception.getClass().getMethod("getFaultInfo");
            ForretningsmessigUnntak faultInfo = (ForretningsmessigUnntak) method.invoke(exception);
            feilbeskrivelse = format("(ForretningsmessigUnntak: feilaarsak: %s, feilkilde: %s, feilmelding: %s%s)",
                    faultInfo.getFeilaarsak(), faultInfo.getFeilkilde(), faultInfo.getFeilmelding(),
                    (nonNull(faultInfo.getTidspunkt()) ? format(", tidspunkt: %s", faultInfo.getTidspunkt().toString()) : ""));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("Lesing af faultInfo fra Aaareg feilet.", e);
        }
        return format("Feil, %s -> %s %s", exception.getClass().getSimpleName(), exception.getMessage(), feilbeskrivelse);
    }

    private static String getUuid(String referanse) {

        return nonNull(referanse) ? referanse : "Dolly: " + UUID.randomUUID().toString();
    }
}
