package no.nav.dolly.bestilling.inntektskomponenten;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.inntektskomponenten.domain.InntektsinformasjonWrapper;
import no.nav.dolly.bestilling.inntektskomponenten.domain.Inntektsinformasjon;
import no.nav.dolly.bestilling.inntektstub.InntektstubConsumer;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;

@Slf4j
@Service
@RequiredArgsConstructor
public class InntektskomponentenClient implements ClientRegister {

    private final InntektstubConsumer inntektstubConsumer;
    private final MapperFacade mapperFacade;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, TpsPerson tpsPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getInntektstub()) && !bestilling.getInntektstub().getInntektsinformasjon().isEmpty()) {

            InntektsinformasjonWrapper inntektsinformasjonWrapper = mapperFacade.map(bestilling.getInntektstub(), InntektsinformasjonWrapper.class);
            inntektsinformasjonWrapper.getInntektsinformasjon().forEach(info -> info.setNorskIdent(tpsPerson.getHovedperson()));

            if (!isOpprettEndre) {
                deleteInntekter(tpsPerson.getHovedperson());
            }
            opprettInntekter(inntektsinformasjonWrapper.getInntektsinformasjon(), progress);
        }
    }

    @Override
    public void release(List<String> identer) {

        identer.forEach(this::deleteInntekter);
    }

    private void opprettInntekter(List<Inntektsinformasjon> inntektsinformasjon, BestillingProgress progress) {

        try {
            ResponseEntity<Inntektsinformasjon[]> response = inntektstubConsumer.postInntekter(inntektsinformasjon);

            if (nonNull(response) && response.hasBody()) {

                progress.setInntektstubStatus(isBlank(response.getBody()[0].getFeilmelding()) ? "OK" : response.getBody()[0].getFeilmelding());

            } else {

                progress.setInntektstubStatus(format("Feilet å opprette inntekter i Inntektstub for ident %s.", inntektsinformasjon.get(0).getNorskIdent()));
            }

        } catch (HttpClientErrorException e) {

            progress.setInntektstubStatus(e.getResponseBodyAsString());

        } catch (RuntimeException e) {

            progress.setInntektstubStatus("Teknisk feil, se logg!");

            log.error("Feilet å opprette inntekter i Inntektstub for ident {}. Feilmelding: {}", inntektsinformasjon.get(0).getNorskIdent(), e.getMessage(), e);
        }

    }

    private void deleteInntekter(String hovedperson) {

        try {
            inntektstubConsumer.deleteInntekter(hovedperson);
        } catch (HttpClientErrorException | HttpServerErrorException e) {

            log.error("Feilet å slette informasjon om ident {} i Inntektstub. Feilmelding: {}", hovedperson, e.getResponseBodyAsString());

        } catch (RuntimeException e) {

            log.error("Feilet å slette informasjon om ident {} i Inntektstub. Feilmelding: {}", hovedperson, e.getMessage());
        }
    }
}
