package no.nav.dolly.bestilling.udistub;

import static java.lang.String.format;
import static no.nav.dolly.sts.StsOidcService.getUserIdToken;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.resultset.udistub.model.Person;
import no.nav.dolly.properties.ProvidersProps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

@Slf4j
@Service
public class UdiStubConsumer {

	private static final String NAV_PERSON_IDENT = "Nav-Personident";
	private static final String UDI_STUB_PERSON = "/api/v1/person";

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ProvidersProps providersProps;

	public ResponseEntity<PersonControllerResponse> createUdiPerson(Long bestillingsid, Person udiPerson) {
		return restTemplate.exchange(RequestEntity.post(URI.create(format("%s%s", providersProps.getUdiStub().getUrl(), UDI_STUB_PERSON)))
				.contentType(MediaType.APPLICATION_JSON)
				.header("Nav-Call-Id", Long.toString(bestillingsid))
				.header("Nav-Consumer-Id", getUserIdToken())
				.body(udiPerson), PersonControllerResponse.class);
	}

	public ResponseEntity<PersonControllerResponse> deleteUdiPerson(Long bestillingsid, String ident) {
		ResponseEntity<PersonControllerResponse> responseEntity = null;

		try {
			responseEntity = restTemplate.exchange(RequestEntity.delete(URI.create(format("%s%s", providersProps.getUdiStub().getUrl(), UDI_STUB_PERSON)))
							.header("Nav-Call-Id", Long.toString(bestillingsid))
							.header("Nav-Consumer-Id", getUserIdToken())
							.header(NAV_PERSON_IDENT, ident)
							.build(),
					PersonControllerResponse.class);
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
				log.warn(String.format("Kunne ikke slette udistub innslag for fnr: %s, da personen ikke ble funnet.", ident));
				return responseEntity;
			} else {
				throw new UdiStubFunctionalException(e.getMessage());
			}
		}

		return responseEntity;
	}

}