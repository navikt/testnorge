package no.nav.dolly.bestilling.udistub;

import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.udistub.UdiStubDefaultPersonUtil.setPersonDefaultsIfUnspecified;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.NorskIdent;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiAlias;
import no.nav.dolly.domain.resultset.udistub.model.UdiAlias;
import no.nav.dolly.domain.resultset.udistub.model.UdiPerson;
import no.nav.dolly.domain.resultset.udistub.model.UdiPersonNavn;

@Slf4j
@Service
public final class UdiStubClient implements ClientRegister {

    @Autowired
    private UdiStubConsumer udiStubConsumer;

    @Autowired
    private MapperFacade mapperFacade;

    @Autowired
    private TpsfService tpsfService;

    @Override
    public void gjenopprett(RsDollyBestilling bestilling, NorskIdent norskIdent, BestillingProgress progress) {

        if (nonNull(bestilling.getUdistub())) {
            StringBuilder status = new StringBuilder();
            ResponseEntity<UdiPersonControllerResponse> response = null;

            try {
                udiStubConsumer.deleteUdiPerson(norskIdent.getIdent());

                UdiPerson udiPerson = mapperFacade.map(bestilling.getUdistub(), UdiPerson.class);
                udiPerson.setIdent(norskIdent.getIdent());
                setPersonDefaultsIfUnspecified(udiPerson);

                RsAliasResponse aliases = createAliases(norskIdent.getIdent(), bestilling.getUdistub().getAliaser(), bestilling.getEnvironments());
                udiPerson.setAliaser(mapperFacade.mapAsList(aliases.getAliaser(), UdiAlias.class));
                udiPerson.setFoedselsDato(aliases.getHovedperson().getFodselsdato().toLocalDate());
                udiPerson.setNavn(mapperFacade.map(aliases.getHovedperson().getNavn(), UdiPersonNavn.class));

                response = udiStubConsumer.createUdiPerson(udiPerson);
                if (response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError()) {
                    String reason = nonNull(response.getBody()) ? response.getBody().getReason() : "Ukjent årsak";
                    throw new UdiStubException(reason);
                }
                appendOkStatus(status, response);

            } catch (UdiStubException e) {

                if (nonNull(response) && response.hasBody() && nonNull(response.getBody().getReason())) {
                    appendErrorStatus(status, e, response.getBody().getReason());
                    log.error("Gjenopprett feilet for udistubclient: {}", response.getBody().getReason(), e);
                } else {
                    appendErrorStatus(status, e, "ukjent årsak");
                    log.error("Gjenopprett feilet for udistubclient: {}", e.getMessage(), e);
                }
            }
            progress.setUdistubStatus(status.toString());
        }
    }

    @Override public void release(List<String> identer) {

        identer.forEach(ident -> udiStubConsumer.deleteUdiPerson(ident));
    }

    private RsAliasResponse createAliases(String ident, List<RsUdiAlias> aliases, List<String> environments) {
        RsAliasRequest aliasRequest = RsAliasRequest.builder()
                .ident(ident)
                .aliaser(mapperFacade.mapAsList(aliases, RsAliasRequest.AliasSpesification.class))
                .environments(environments)
                .build();

        return tpsfService.createAliases(aliasRequest).getBody();
    }

    private static void appendOkStatus(StringBuilder status, ResponseEntity<UdiPersonControllerResponse> postResponse) {
        if (nonNull(postResponse) && postResponse.hasBody()) {
            status.append("OK");
        }
    }

    private static void appendErrorStatus(StringBuilder status, RuntimeException e, String reason) {
        status.append("FEIL: ")
                .append(e.getMessage())
                .append(": ")
                .append(reason);

        if (e instanceof HttpClientErrorException) {
            status.append(" (")
                    .append(encodeErrorStatus(((HttpClientErrorException) e).getResponseBodyAsString()))
                    .append(')');
        }
    }

    private static String encodeErrorStatus(String toBeEncoded) {
        return toBeEncoded.replaceAll(",", "&").replaceAll(":", "=");
    }
}
