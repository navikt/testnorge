package no.nav.dolly.bestilling.udistub;

import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.udistub.UdiStubDefaultPersonUtil.setPersonDefaultsIfUnspecified;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.NorskIdent;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.udistub.model.MangelfullDato;
import no.nav.dolly.domain.resultset.udistub.model.Person;
import no.nav.dolly.domain.resultset.udistub.model.PersonNavn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Service
public class UdiStubClient implements ClientRegister {

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

            try {
                String fnr = norskIdent.getIdent();
                ResponseEntity<PersonControllerResponse> deleteResponse = udiStubConsumer.deleteUdiPerson(progress.getBestillingId(), fnr);

                if (isOkStatus(deleteResponse)) {
                    Person udiPerson = mapperFacade.map(bestilling.getUdistub(), Person.class);

                    no.nav.dolly.domain.resultset.Person tpsfPerson = tpsfService.hentPersonWithIdent(fnr);
                    insertFieldsFromTpsfPerson(udiPerson, tpsfPerson);
                    setPersonDefaultsIfUnspecified(udiPerson);

                    ResponseEntity<PersonControllerResponse> postResponse = udiStubConsumer.createUdiPerson(progress.getBestillingId(), udiPerson);

                    appendOkStatus(status, postResponse);
                }
            } catch (RuntimeException e) {
                appendErrorStatus(status, e);
                log.error("Gjenopprett feilet for udistubclient: {}", e.getMessage(), e);
            }
            progress.setUdistubStatus(status.toString());
        }
    }

    private void insertFieldsFromTpsfPerson(Person udiPerson, no.nav.dolly.domain.resultset.Person tpsfPerson) {
        PersonNavn tpsfPersonNavn = new PersonNavn(tpsfPerson.getFornavn(), null, tpsfPerson.getEtternavn());

        udiPerson.setFnr(tpsfPerson.getIdent());
        udiPerson.setNavn(tpsfPersonNavn);
        udiPerson.getAliaser().forEach(alias ->
        {
            alias.setFnr(tpsfPerson.getIdent());
            alias.setNavn(tpsfPersonNavn);
        });
        udiPerson.setFoedselsDato(extractFodselsDatoFromIdent(tpsfPerson.getIdent()));
    }

    private MangelfullDato extractFodselsDatoFromIdent(String ident) {
        return new MangelfullDato(
                Integer.valueOf(ident.substring(0, 2)),
                Integer.valueOf(ident.substring(2, 4)),
                Integer.valueOf(ident.substring(4, 6))
        );
    }


    private boolean isOkStatus(ResponseEntity<PersonControllerResponse> response) {
        return HttpStatus.OK == response.getStatusCode() ||
                HttpStatus.CREATED == response.getStatusCode() ||
                HttpStatus.ACCEPTED == response.getStatusCode();
    }

    private static void appendOkStatus(StringBuilder status, ResponseEntity<PersonControllerResponse> postResponse) {
        if (postResponse.hasBody()) {
            Person createdPerson = postResponse.getBody().getPerson();
            status.append(',')
                    .append("fnr=")
                    .append(createdPerson.getFnr())
                    .append(postResponse.getStatusCode().getReasonPhrase());
        }
    }

    private static void appendErrorStatus(StringBuilder status, RuntimeException e) {
        status.append("Feil: ")
                .append(e.getMessage());

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
