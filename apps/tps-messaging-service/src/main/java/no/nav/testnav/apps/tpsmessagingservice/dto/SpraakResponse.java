package no.nav.testnav.apps.tpsmessagingservice.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.xml.bind.annotation.XmlRootElement;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@XmlRootElement(name = "sfePersonData")
public class SpraakResponse extends EndringsmeldingResponse {

    private SpraakRequest.SfeAjourforing sfeAjourforing;
}