package no.nav.testnav.apps.tpsmessagingservice.dto;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.xml.bind.annotation.XmlRootElement;

@SuperBuilder
@NoArgsConstructor
@XmlRootElement(name = "sfePersonData")
public class SfePersonDataErrorResponse extends EndringsmeldingResponse {
}
