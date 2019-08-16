package no.nav.dolly.bestilling.udistub;

import static java.lang.String.format;
import static no.nav.dolly.sts.StsOidcService.getUserIdToken;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.resultset.udistub.UdiForholdRequest;
import no.nav.dolly.properties.ProvidersProps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Slf4j
@Service
public class UdiStubConsumer {

	private static final String UDI_STUB_UDIFORHOLD = "/api/v1/person";

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ProvidersProps providersProps;

		public ResponseEntity<Object> createUdiForhold(Long bestillingsid, UdiForholdRequest udiForholdRequest) {
			return restTemplate.exchange(RequestEntity.post(URI.create(format("%s%s", providersProps.getUdiStub().getUrl(), UDI_STUB_UDIFORHOLD)))
					.contentType(MediaType.APPLICATION_JSON)
					.header("Nav-Call-Id", Long.toString(bestillingsid))
					.header("Nav-Consumer-Id", getUserIdToken())
					.body(udiForholdRequest), Object.class);
		}

		public ResponseEntity deleteUdiForhold(Long bestillingsid, String ident) {
			return restTemplate.exchange(RequestEntity.delete(URI.create(format("%s%s", providersProps.getUdiStub().getUrl(), UDI_STUB_UDIFORHOLD)))
							.header("Nav-Call-Id", Long.toString(bestillingsid))
							.header("Nav-Consumer-Id", getUserIdToken())
							.header("personidentifikator", ident)
							.build(),
					String.class);
	}

}