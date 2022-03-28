package no.nav.dolly.bestilling.inntektstub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;
import no.nav.dolly.bestilling.inntektstub.domain.InntektsinformasjonWrapper;
import no.nav.dolly.bestilling.inntektstub.util.CompareUtil;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class InntektstubClient implements ClientRegister {

    private final InntektstubConsumer inntektstubConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getInntektstub()) && !bestilling.getInntektstub().getInntektsinformasjon().isEmpty()) {

            bestilling.getInntektstub().getInntektsinformasjon()
                    .forEach(inntekter ->
                            inntekter.getInntektsliste()
                                    .forEach(inntekt ->
                                            inntekt.setTilleggsinformasjon(isNull(inntekt.getTilleggsinformasjon()) ||
                                                    inntekt.getTilleggsinformasjon().isEmpty() ? null :
                                                    inntekt.getTilleggsinformasjon())));

            try {
                InntektsinformasjonWrapper inntektsinformasjonWrapper = mapperFacade.map(bestilling.getInntektstub(), InntektsinformasjonWrapper.class);
                inntektsinformasjonWrapper.getInntektsinformasjon().forEach(info -> info.setNorskIdent(dollyPerson.getHovedperson()));

                if (isOpprettEndre || !existInntekter(inntektsinformasjonWrapper.getInntektsinformasjon())) {
                    opprettInntekter(inntektsinformasjonWrapper.getInntektsinformasjon(), progress);
                } else {
                    progress.setInntektstubStatus("OK");
                }

            } catch (RuntimeException e) {
                progress.setInntektstubStatus(errorStatusDecoder.decodeRuntimeException(e));
            }
        }
    }

    @Override
    public void release(List<String> identer) {

        identer.forEach(this::deleteInntekter);
    }

    private boolean existInntekter(List<Inntektsinformasjon> inntekterRequest) {

        ResponseEntity<List<Inntektsinformasjon>> inntekter =
                inntektstubConsumer.getInntekter(inntekterRequest.get(0).getNorskIdent());

        if (inntekter.hasBody() && !inntekter.getBody().isEmpty()) {

            return CompareUtil.isSubsetOf(inntekterRequest, inntekter.getBody());
        }
        return false;
    }

    private void opprettInntekter(List<Inntektsinformasjon> inntektsinformasjon, BestillingProgress progress) {

        try {
            ResponseEntity<List<Inntektsinformasjon>> response = inntektstubConsumer.postInntekter(inntektsinformasjon);

            if (response.hasBody() && !response.getBody().isEmpty()) {

                progress.setInntektstubStatus(response.getBody().stream()
                        .filter(inntekt -> isNotBlank(inntekt.getFeilmelding()))
                        .findFirst()
                        .orElse(Inntektsinformasjon.builder()
                                .feilmelding("OK")
                                .build())
                        .getFeilmelding());

            } else {

                progress.setInntektstubStatus(format("Feilet å opprette inntekter i Inntektstub for ident %s.", inntektsinformasjon.get(0).getNorskIdent()));
            }

        } catch (RuntimeException e) {

            progress.setInntektstubStatus(errorStatusDecoder.decodeRuntimeException(e));

            log.error("Feilet å opprette inntekter i Inntektstub for ident {}. Feilmelding: {}", inntektsinformasjon.get(0).getNorskIdent(), e.getMessage(), e);
        }
    }

    private void deleteInntekter(String hovedperson) {

        try {
            ResponseEntity<Inntektsinformasjon> response = inntektstubConsumer.deleteInntekter(hovedperson);
            log.info("Slettet inntektsinformasjon om ident {} i Inntektstub: {}", hovedperson, nonNull(response) ? response.getBody() : null);

        } catch (WebClientResponseException e) {

            log.error("Feilet å slette informasjon om ident {} i Inntektstub. Feilmelding: {}", hovedperson, e.getResponseBodyAsString());

        } catch (RuntimeException e) {

            log.error("Feilet å slette informasjon om ident {} i Inntektstub. Feilmelding: {}", hovedperson, e.getMessage());
        }
    }
}
